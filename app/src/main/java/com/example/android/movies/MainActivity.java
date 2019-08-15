package com.example.android.movies;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.movieMeProcessor;
import com.example.android.movies.utilities.moviesAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements moviesAdapter.ListItemClickListener, moviesAdapter.ButtonItemClickListener {

    private static ArrayList<Movie> movies = new ArrayList();
    private static ArrayList<Movie> favMovies = new ArrayList();
    private static RecyclerView moviesGrid;
    private static moviesAdapter mAdapter;
    private static final int NUM_LIST_MOVIES = 100;
    private String isFavorite;
    private AppDatabase mDb;
    static List<FavEntry> favorites;
    private static int resumeCode;
    private String movieID;
    private String INSTANCE_RESUME_CODE = "RESUME_CODE";
    private String INSTANCE_VIEW_POSITION_CODE = "POSITION CODE";
    private static int viewHolderPosition;
    private static int NUM_LIST_MOVIES_FAVORITES;
    private String favorite;
    private FavEntry movieEntry;
    private static int asyncCount;
    private int buttonClick;
    private ImageButton imgButtonPop;
    private ImageButton imgButtonFav;
    private ImageButton imgButtonTop;
    private ImageButton imgButtonAction;
    private ImageButton imgButtonAdventure;
    private ImageButton imgButtonComedy;
    private ImageButton imgButtonHistory;
    private ImageButton imgButtonHorror;
    private ImageButton imgButtonDrama;
    private ImageButton imgButtonFantasy;
    private ImageButton imgButtonMystery;
    private ImageButton imgButtonRomance;
    private ImageButton imgButtonScifi;
    private ImageButton imgButtonThriller;
    private ImageButton imgButtonWestern;
    private movieMeProcessor movieMeProcessor;
    private static Context mContext;
    private static int statusCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey(getString(R.string.ID))) {
            String movieIDQuery = getString(R.string.API_Query_Fav_Base) + extras.get(getString(R.string.ID)) + "?" + getString(R.string.API_key_append) + getString(R.string.API_key) + "&" + getString(R.string.API_Query_Videos_End);

            URL movieURL = null;
            try {
                movieURL = new URL(movieIDQuery);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            new apiCallFCM().execute(movieURL);
        }

        mContext = this;

        imgButtonPop = (ImageButton) findViewById(R.id.imageButton_pop);
        imgButtonFav = (ImageButton) findViewById(R.id.imageButton_favorites);
        imgButtonTop = (ImageButton) findViewById(R.id.imageButton_top);
        imgButtonAction = (ImageButton) findViewById(R.id.imageButton_action);
        imgButtonAdventure = (ImageButton) findViewById(R.id.imageButton_adventure);
        imgButtonComedy = (ImageButton) findViewById(R.id.imageButton_comedy);
        imgButtonHistory = (ImageButton) findViewById(R.id.imageButton_history);
        imgButtonHorror = (ImageButton) findViewById(R.id.imageButton_horror);
        imgButtonDrama = (ImageButton) findViewById(R.id.imageButton_drama);
        imgButtonFantasy = (ImageButton) findViewById(R.id.imageButton_fantasy);
        imgButtonMystery = (ImageButton) findViewById(R.id.imageButton_mystery);
        imgButtonRomance = (ImageButton) findViewById(R.id.imageButton_romance);
        imgButtonScifi = (ImageButton) findViewById(R.id.imageButton_scifi);
        imgButtonThriller = (ImageButton) findViewById(R.id.imageButton_thriller);
        imgButtonWestern = (ImageButton) findViewById(R.id.imageButton_western);


        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_RESUME_CODE)) {
            resumeCode = savedInstanceState.getInt(INSTANCE_RESUME_CODE);
            viewHolderPosition = savedInstanceState.getInt(INSTANCE_VIEW_POSITION_CODE);
            Log.d("TEST", "Resume code: " + resumeCode);
        } else {
            resumeCode = 1;
        }

        buttonClick = 0;
        statusCode = 0;


        moviesGrid = (RecyclerView) findViewById(R.id.movie_items);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        moviesGrid.setLayoutManager(layoutManager);
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, this, movies, favMovies);

        moviesGrid.setAdapter(mAdapter);
        moviesGrid.setHasFixedSize(true);
        mDb = AppDatabase.getInstance(getApplicationContext());

        setupViewModel();


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_RESUME_CODE, resumeCode);
        outState.putInt(INSTANCE_VIEW_POSITION_CODE, viewHolderPosition);
        super.onSaveInstanceState(outState);
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavs().observe(this, new Observer<List<FavEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavEntry> favEntries) {
                Log.d("T11", "onChanged viewModel");
                favorites = favEntries;
                movieMeProcessor = new movieMeProcessor(favorites);

                favMovies.clear();

                for (FavEntry a : favorites) {
                    Movie addMovie = new Movie();
                    addMovie.setMovieName(a.getName());
                    favMovies.add(addMovie);
                }

                Log.d("T11", "Button click is: " + buttonClick);

                if (buttonClick == 0 && resumeCode == 1) {
                    populateUI(getString(R.string.Most_Popular));
                } else if (buttonClick == 0 && resumeCode == 2) {
                    populateUI(getString(R.string.Top_Rated));
                } else if (buttonClick == 0 && resumeCode == 4) {
                    populateUI(getString(R.string.Action));
                } else if (buttonClick == 0 && resumeCode == 5) {
                    populateUI(getString(R.string.Adventure));
                } else if (buttonClick == 0 && resumeCode == 6) {
                    populateUI(getString(R.string.Comedy));
                } else if (buttonClick == 0 && resumeCode == 7) {
                    populateUI(getString(R.string.History));
                } else if (buttonClick == 0 && resumeCode == 8) {
                    populateUI(getString(R.string.Horror));
                } else if (buttonClick == 0 && resumeCode == 9) {
                    populateUI(getString(R.string.Drama));
                } else if (buttonClick == 0 && resumeCode == 10) {
                    populateUI(getString(R.string.Fantasy));
                } else if (buttonClick == 0 && resumeCode == 11) {
                    populateUI(getString(R.string.Mystery));
                } else if (buttonClick == 0 && resumeCode == 12) {
                    populateUI(getString(R.string.Romance));
                } else if (buttonClick == 0 && resumeCode == 13) {
                    populateUI(getString(R.string.Science_Fiction));
                } else if (buttonClick == 0 && resumeCode == 14) {
                    populateUI(getString(R.string.Thriller));
                } else if (buttonClick == 0 && resumeCode == 15) {
                    populateUI(getString(R.string.Western));
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        asyncCount = 0;
        buttonClick = 0;
    }

    public void setCategoryButtonsColor() {

        imgButtonPop.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonFav.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonTop.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonAction.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonAdventure.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonComedy.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonHistory.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonHorror.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonDrama.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonFantasy.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonMystery.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonRomance.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonScifi.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonThriller.setBackground(getDrawable(R.drawable.circle_border_black));
        imgButtonWestern.setBackground(getDrawable(R.drawable.circle_border_black));

        if (resumeCode == 1) {
            imgButtonPop.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 2) {
            imgButtonTop.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 3) {
            imgButtonFav.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 4) {
            imgButtonAction.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 5) {
            imgButtonAdventure.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 6) {
            imgButtonComedy.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 7) {
            imgButtonHistory.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 8) {
            imgButtonHorror.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 9) {
            imgButtonDrama.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 10) {
            imgButtonFantasy.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 11) {
            imgButtonMystery.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 12) {
            imgButtonRomance.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 13) {
            imgButtonScifi.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 14) {
            imgButtonThriller.setBackground(getDrawable(R.drawable.circle_border_red));
        } else if (resumeCode == 15) {
            imgButtonWestern.setBackground(getDrawable(R.drawable.circle_border_red));
        }

    }

    public void populateUI(String category) {

        if (category.equals(getString(R.string.Most_Popular))) {
            setMoviesFromCategory(getString(R.string.Most_Popular));
            setTitle(getString(R.string.Most_Popular));
        } else if (category.equals(getString(R.string.Top_Rated))) {
            setMoviesFromCategory(getString(R.string.Top_Rated));
            setTitle(getString(R.string.Top_Rated));
        } else if (category.equals(getString(R.string.Favorites))) {
            setMoviesFavorites();
            setTitle(getString(R.string.Favorites));
        } else if (category.equals(getString(R.string.Action))) {
            setMoviesFromCategory(getString(R.string.Action));
            setTitle(getString(R.string.Action));
        } else if (category.equals(getString(R.string.Adventure))) {
            setMoviesFromCategory(getString(R.string.Adventure));
            setTitle(getString(R.string.Adventure));
        } else if (category.equals(getString(R.string.Comedy))) {
            setMoviesFromCategory(getString(R.string.Comedy));
            setTitle(getString(R.string.Comedy));
        } else if (category.equals(getString(R.string.History))) {
            setMoviesFromCategory(getString(R.string.History));
            setTitle(getString(R.string.History));
        } else if (category.equals(getString(R.string.Horror))) {
            setMoviesFromCategory(getString(R.string.Horror));
            setTitle(getString(R.string.Horror));
        } else if (category.equals(getString(R.string.Drama))) {
            setMoviesFromCategory(getString(R.string.Drama));
            setTitle(getString(R.string.Drama));
        } else if (category.equals(getString(R.string.Fantasy))) {
            setMoviesFromCategory(getString(R.string.Fantasy));
            setTitle(getString(R.string.Fantasy));
        } else if (category.equals(getString(R.string.Mystery))) {
            setMoviesFromCategory(getString(R.string.Mystery));
            setTitle(getString(R.string.Mystery));
        } else if (category.equals(getString(R.string.Romance))) {
            setMoviesFromCategory(getString(R.string.Romance));
            setTitle(getString(R.string.Romance));
        } else if (category.equals(getString(R.string.Science_Fiction))) {
            setMoviesFromCategory(getString(R.string.Science_Fiction));
            setTitle(getString(R.string.Science_Fiction));
        } else if (category.equals(getString(R.string.Thriller))) {
            setMoviesFromCategory(getString(R.string.Thriller));
            setTitle(getString(R.string.Thriller));
        } else if (category.equals(getString(R.string.Western))) {
            setMoviesFromCategory(getString(R.string.Western));
            setTitle(getString(R.string.Western));
        }

        setCategoryButtonsColor();

    }

    public void populateUIFavorites() {
        asyncCount = 0;

        setMoviesFavorites();
        setCategoryButtonsColor();
    }

    private static void scrollToPosition() {
        if (resumeCode > 0) {
            if (resumeCode == 3 && viewHolderPosition == NUM_LIST_MOVIES_FAVORITES) {
                viewHolderPosition--;
            }
            Log.d("TEST", "Scrolling to: " + viewHolderPosition);
            moviesGrid.scrollToPosition(viewHolderPosition);
        }
    }

    public void MostPopClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Most_Popular));
    }

    public void FavsClick(View view) {
        viewHolderPosition = 0;

        if (favMovies.size() == 0) {
            Toast.makeText(this, "Add a favorite movie first!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("T11", "Size: " + favMovies.size());
            populateUIFavorites();
        }
    }

    public void TopRatedClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Top_Rated));
    }

    public void AcitonClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Action));
    }

    public void AdventureClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Adventure));
    }

    public void ComedyClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Comedy));
    }

    public void HistoryClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.History));
    }

    public void HorrorClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Horror));
    }

    public void DramaClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Drama));
    }

    public void FantasyClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Fantasy));
    }

    public void MysteryClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Mystery));
    }

    public void RomanceClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Romance));
    }

    public void ScifiClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Science_Fiction));
    }

    public void ThrillerClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Thriller));
    }

    public void WesternClick(View view) {
        viewHolderPosition = 0;
        populateUI(getString(R.string.Western));
    }

    public void setMoviesFromCategory(String category) {

        statusCode = 0;

        movies = new ArrayList();

        String resultsString = "";
        String MoviesCategory = category;
        String sortedBy = "";

        if (MoviesCategory.equals(getString(R.string.Top_Rated))) {
            resumeCode = 2;
            sortedBy = getString(R.string.API_Query_TopRated_Desc);
        } else if (MoviesCategory.equals(getString(R.string.Most_Popular))) {
            resumeCode = 1;
            sortedBy = getString(R.string.API_Query_MostPop);
        } else if (MoviesCategory.equals(getString(R.string.Action))) {
            resumeCode = 4;
            sortedBy = getString(R.string.API_Query_Genre_Action) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Adventure))) {
            resumeCode = 5;
            sortedBy = getString(R.string.API_Query_Genre_Adventure) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Comedy))) {
            resumeCode = 6;
            sortedBy = getString(R.string.API_Query_Genre_Comedy) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.History))) {
            resumeCode = 7;
            sortedBy = getString(R.string.API_Query_Genre_History) + getString(R.string.API_Search_Part5);
            Log.d("T10", sortedBy);
        } else if (MoviesCategory.equals(getString(R.string.Horror))) {
            resumeCode = 8;
            sortedBy = getString(R.string.API_Query_Genre_Horror) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Drama))) {
            resumeCode = 9;
            sortedBy = getString(R.string.API_Query_Genre_Drama) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Fantasy))) {
            resumeCode = 10;
            sortedBy = getString(R.string.API_Query_Genre_Fantasy) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Mystery))) {
            resumeCode = 11;
            sortedBy = getString(R.string.API_Query_Genre_Mystery) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Romance))) {
            resumeCode = 12;
            sortedBy = getString(R.string.API_Query_Genre_Romance) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Science_Fiction))) {
            resumeCode = 13;
            sortedBy = getString(R.string.API_Query_Genre_Science_Fiction) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Thriller))) {
            resumeCode = 14;
            sortedBy = getString(R.string.API_Query_Genre_Thriller) + getString(R.string.API_Search_Part5);
        } else if (MoviesCategory.equals(getString(R.string.Western))) {
            resumeCode = 15;
            sortedBy = getString(R.string.API_Query_Genre_Western) + getString(R.string.API_Search_Part5);
        }

        for (int i = 1; i < 6; i++) {
            String pageNum = Integer.toString(i);
            URL testURL = NetworkUtils.jsonRequest(sortedBy, pageNum);
            Log.d("T10", "URL: " + testURL);
            new apiCall().execute(testURL);
        }

    }

    public void setMoviesFavorites() {
        resumeCode = 3;
        movies = new ArrayList();

        for (FavEntry a : favorites) {
            String favID = a.getId() + "?";

            String movieIDQuery = getString(R.string.API_Query_Fav_Base) + favID + getString(R.string.API_key_append) + getString(R.string.API_key) + "&" + getString(R.string.API_Query_Videos_End);

            Log.d("T12", movieIDQuery);

            URL movieURL = null;
            try {
                movieURL = new URL(movieIDQuery);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            new apiCallFavs().execute(movieURL);
        }

    }

    public static void executeFCM(Movie firebaseMovie) {
        Intent goToMovieActivity = new Intent(mContext, movieActivity.class);

        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Name), firebaseMovie.getMovieName());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Img_Url), firebaseMovie.getImageURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Synopsis), firebaseMovie.getSynopsis());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Rating), firebaseMovie.getUserRating());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Release_Date), firebaseMovie.getReleaseDate());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID_URL), firebaseMovie.getMovieIdURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID), firebaseMovie.getId());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Backdrop), firebaseMovie.getBackdropURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Genre), firebaseMovie.getGenre());

        String movieID = firebaseMovie.getId();
        String isFavorite = mContext.getString(R.string.No);

        for (FavEntry a : favorites) {
            if (a.getId().equals(movieID)) {
                isFavorite = mContext.getString(R.string.Yes);
                Log.d("TEST", "onListItemClick marking favorite as YES");
            }
        }

        goToMovieActivity.putExtra(mContext.getString(R.string.Is_Fav_Key), isFavorite);

        mContext.startActivity(goToMovieActivity);
    }

    public void onListItemClick(int clickedItemIndex) {

        if (!movies.isEmpty()) {
            Context context = MainActivity.this;
            Class destination = movieActivity.class;

            viewHolderPosition = clickedItemIndex;

            Log.d("TEST", "Viewholder position is: " + viewHolderPosition);

            final Intent goToMovieActivity = new Intent(context, destination);

            goToMovieActivity.putExtra(getString(R.string.Movie_Name), movies.get(clickedItemIndex).getMovieName());
            goToMovieActivity.putExtra(getString(R.string.Movie_Img_Url), movies.get(clickedItemIndex).getImageURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_Synopsis), movies.get(clickedItemIndex).getSynopsis());
            goToMovieActivity.putExtra(getString(R.string.Movie_Rating), movies.get(clickedItemIndex).getUserRating());
            goToMovieActivity.putExtra(getString(R.string.Movie_Release_Date), movies.get(clickedItemIndex).getReleaseDate());
            goToMovieActivity.putExtra(getString(R.string.Movie_ID_URL), movies.get(clickedItemIndex).getMovieIdURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_ID), movies.get(clickedItemIndex).getId());
            goToMovieActivity.putExtra(getString(R.string.Movie_Backdrop), movies.get(clickedItemIndex).getBackdropURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_Genre), movies.get(clickedItemIndex).getGenre());

            movieID = movies.get(clickedItemIndex).getId();
            isFavorite = getString(R.string.No);

            for (FavEntry a : favorites) {
                if (a.getId().equals(movieID)) {
                    isFavorite = getString(R.string.Yes);
                    Log.d("TEST", "onListItemClick marking favorite as YES");
                }
            }

            goToMovieActivity.putExtra(getString(R.string.Is_Fav_Key), isFavorite);

            startActivity(goToMovieActivity);
        } else {
            Log.d("TEST", "ERROR");
        }
    }

    public static void goToMovieMeDetail(Movie movieMe, Context context) {
        mContext = context;
        Class destination = movieActivity.class;

        final Intent goToMovieActivity = new Intent(context, destination);

        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Name), movieMe.getMovieName());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Img_Url), movieMe.getImageURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Synopsis), movieMe.getSynopsis());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Rating), movieMe.getUserRating());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Release_Date), movieMe.getReleaseDate());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID_URL), movieMe.getMovieIdURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID), movieMe.getId());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Backdrop), movieMe.getBackdropURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Genre), movieMe.getGenre());
        goToMovieActivity.putExtra(mContext.getString(R.string.Is_Fav_Key), mContext.getString(R.string.No));

        mContext.startActivity(goToMovieActivity);
    }

    @Override
    public void onButtonClick(final int clickedItemIndex) {

        if (!movies.isEmpty()) {
            Log.d("TEST", "PLEASE: " + clickedItemIndex);

            favorite = getString(R.string.No);
            buttonClick = 1;

            for (FavEntry a : favorites) {
                if (a.getId().equals(movies.get(clickedItemIndex).getId())) {
                    favorite = getString(R.string.Yes);
                    movieEntry = a;
                    Log.d("TEST", "Movie is a favorite");
                }
            }

            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (favorite.equals(getString(R.string.Yes))) {
                        mDb.favDao().deleteFav(movieEntry);
                        favorite = getString(R.string.No);
                    } else {
                        FavEntry enterNewFavorite = new FavEntry(movies.get(clickedItemIndex).getId(), movies.get(clickedItemIndex).getMovieName(), movies.get(clickedItemIndex).getGenre(), movies.get(clickedItemIndex).getUserRating());
                        mDb.favDao().insertFav(enterNewFavorite);
                        favorite = getString(R.string.Yes);
                    }
                }
            });
        } else {
            Log.d("TEST", "ERROR");
        }

    }


    public void onFabClicked(View v) {
        Log.d("FAB1", "FAB CLICKED");

        if (!movies.isEmpty()) {

            if (favMovies.size() < 10) {
                Toast.makeText(this, "Please select at least 10 favs first!", Toast.LENGTH_LONG).show();
            } else {
                int checkCode = 0;
                String movieIDQuery = "";
                String resultsString = "";

                ArrayList<String> result = movieMeProcessor.process();
                statusCode = 1;
                Random rand = new Random();


                movieIDQuery = getString(R.string.API_Search_Part1) + getString(R.string.API_key) + getString(R.string.API_Search_Part2)
                        + (rand.nextInt(10) + 1) + getString(R.string.API_Search_Part3) + result.get(1) + getString(R.string.API_Search_Part4) + result.get(0)
                        + getString(R.string.API_Search_Part5);

                Log.d("T14", movieIDQuery);

                resultsString = "";

                URL testURL = null;
                try {
                    testURL = new URL(movieIDQuery);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new apiCallButton().execute(testURL);

                if (resultsString != null && resultsString.length() > 200) {
                    checkCode = 1;
                }

                Log.d("FAB1", movieIDQuery);


            }
        } else {
            Log.d("TEST", "ERROR");
        }
    }

    public static void executeFavButton(String apiResults) {
        int favCheck = 0;
        Movie movieMe = new Movie();
        Random rand = new Random();

        ArrayList<Movie> movieMeResults;
        movieMeResults = JsonUtils.parseApiResult(apiResults);

        while (favCheck == 0) {
            movieMe = movieMeResults.get(rand.nextInt(movieMeResults.size()));

            favCheck = 1;

            for (Movie b : favMovies) {
                if (b.getMovieName().equals(movieMe.getMovieName())) {
                    favCheck = 0;
                }

            }

            if (movieMe.getBackdropURL() == "") {
                favCheck = 0;
            }

            if (favCheck == 1) {
                Log.d("FAB1", movieMe.getMovieName());
            } else {
                Log.d("FAB1", "Recommended a favorite starting over");
            }

        }

        if (movieMe.getMovieName() != null) {
            goToMovieMeDetail(movieMe, mContext);
        }
    }

    public static void executeFavs() {
        Log.d("t7", "executing favs");
        mAdapter.setNumberMovies(favMovies.size());
        mAdapter.setMovies(movies);
        moviesGrid.setAdapter(mAdapter);
        scrollToPosition();
    }


    public static void execute() {
        Log.d("FAV2", "is empty: " + favMovies.isEmpty());
        asyncCount = 0;
        mAdapter.setNumberMovies(NUM_LIST_MOVIES);
        mAdapter.setMovies(movies);
        moviesGrid.setAdapter(mAdapter);
        scrollToPosition();
    }

    public static int getNumFavs() {
        return favMovies.size();
    }

    public static List<Movie> getFavMovies() {
        return favMovies;
    }

    public static class apiCallButton extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            Log.d("T8", "doing in background");
            URL apiCall = urls[0];
            String apiResult = null;


            try {
                apiResult = NetworkUtils.getResponseFromHttpUrl(apiCall);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return apiResult;
        }

        @Override
        protected void onPostExecute(String apiResults) {
            if (apiResults != null) {
                executeFavButton(apiResults);
            } else {
                Log.d("T14", "EMPTY");
                Toast.makeText(mContext, "Movie DB not responding", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static class apiCall extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            Log.d("T8", "doing in background");
            URL apiCall = urls[0];
            String apiResult = null;


            try {
                apiResult = NetworkUtils.getResponseFromHttpUrl(apiCall);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return apiResult;
        }

        @Override
        protected void onPostExecute(String apiResults) {

            if (apiResults == null) {
                Toast.makeText(mContext, "API error", Toast.LENGTH_SHORT).show();
            }

            ArrayList<Movie> moviesAdd;
            moviesAdd = JsonUtils.parseApiResult(apiResults);

            for (Movie movie : moviesAdd) {

                for (FavEntry a : favorites) {
                    if (a.getName().equals(movie.getMovieName())) {
                        Log.d("T4", "YES");
                        movie.setFav("Yes");
                    }
                }
                movies.add(movie);
                Log.d("T8", "movies size is: " + movies.size());
            }

            if (movies.size() > 99) {
                MainActivity.execute();
            }

        }
    }

    public static class apiCallFCM extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            Log.d("T7", "doing in background");
            URL apiCall = urls[0];
            String apiResult = null;


            try {
                apiResult = NetworkUtils.getResponseFromHttpUrl(apiCall);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return apiResult;
        }

        @Override
        protected void onPostExecute(String apiResults) {

            Movie addMovie = JsonUtils.parseFavoriteMovie(apiResults);
            MainActivity.executeFCM(addMovie);
        }
    }

    public static class apiCallFavs extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            Log.d("T7", "doing in background");
            URL apiCall = urls[0];
            String apiResult = null;


            try {
                apiResult = NetworkUtils.getResponseFromHttpUrl(apiCall);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return apiResult;
        }

        @Override
        protected void onPostExecute(String apiResults) {

            Movie addMovie = JsonUtils.parseFavoriteMovie(apiResults);

            if (addMovie != null) {
                addMovie.setFav("Yes");
                movies.add(addMovie);
                Log.d("T7", "movies size is: " + movies.size());
                Log.d("T7", "fav movies size is: " + favMovies.size());
            }

            if (movies.size() == favMovies.size()) {
                MainActivity.executeFavs();
            }

        }
    }
}

