package com.example.android.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.moviesAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinWorkerThread;

public class MainActivity extends AppCompatActivity implements moviesAdapter.ListItemClickListener, moviesAdapter.ButtonItemClickListener {

    private static ArrayList<Movie> movies = new ArrayList();
    private static ArrayList<Movie> favMovies = new ArrayList();
    private static RecyclerView moviesGrid;
    private static moviesAdapter mAdapter;
    private static final int NUM_LIST_MOVIES = 100;
    private String isFavorite;
    private AppDatabase mDb;
    List<FavEntry> favorites;
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
    private int category;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;

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


        moviesGrid = (RecyclerView) findViewById(R.id.movie_items);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        moviesGrid.setLayoutManager(layoutManager);
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, this, movies, favMovies);

        moviesGrid.setAdapter(mAdapter);
        moviesGrid.setHasFixedSize(true);
        mDb = AppDatabase.getInstance(getApplicationContext());

        setupViewModel();

    }


    public static void execute() {

        if (resumeCode != 3) {
            asyncCount++;
        }
        Log.d("async", "Count is: " + asyncCount);

        if (asyncCount == 5) {


            Log.d("FAV2", "is empty: " + favMovies.isEmpty());

            asyncCount = 0;
            mAdapter.setNumberMovies(NUM_LIST_MOVIES);
            mAdapter.setMovies(movies);
            moviesGrid.setAdapter(mAdapter);
            scrollToPosition();
        }

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
                favorites = favEntries;

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
        NUM_LIST_MOVIES_FAVORITES = movies.size();
        mAdapter.setNumberMovies(NUM_LIST_MOVIES_FAVORITES);
        mAdapter.setMovies(movies);
        moviesGrid.setAdapter(mAdapter);
        setTitle(R.string.Set_Title_Favorite);

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
        populateUIFavorites();
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
            sortedBy = getString(R.string.API_Query_Genre_Action);
        } else if (MoviesCategory.equals(getString(R.string.Adventure))) {
            resumeCode = 5;
            sortedBy = getString(R.string.API_Query_Genre_Adventure);
        } else if (MoviesCategory.equals(getString(R.string.Comedy))) {
            resumeCode = 6;
            sortedBy = getString(R.string.API_Query_Genre_Comedy);
        } else if (MoviesCategory.equals(getString(R.string.History))) {
            resumeCode = 7;
            sortedBy = getString(R.string.API_Query_Genre_History);
        } else if (MoviesCategory.equals(getString(R.string.Horror))) {
            resumeCode = 8;
            sortedBy = getString(R.string.API_Query_Genre_Horror);
        } else if (MoviesCategory.equals(getString(R.string.Drama))) {
            resumeCode = 9;
            sortedBy = getString(R.string.API_Query_Genre_Drama);
        } else if (MoviesCategory.equals(getString(R.string.Fantasy))) {
            resumeCode = 10;
            sortedBy = getString(R.string.API_Query_Genre_Fantasy);
        } else if (MoviesCategory.equals(getString(R.string.Mystery))) {
            resumeCode = 11;
            sortedBy = getString(R.string.API_Query_Genre_Mystery);
        } else if (MoviesCategory.equals(getString(R.string.Romance))) {
            resumeCode = 12;
            sortedBy = getString(R.string.API_Query_Genre_Romance);
        } else if (MoviesCategory.equals(getString(R.string.Science_Fiction))) {
            resumeCode = 13;
            sortedBy = getString(R.string.API_Query_Genre_Science_Fiction);
        } else if (MoviesCategory.equals(getString(R.string.Thriller))) {
            resumeCode = 14;
            sortedBy = getString(R.string.API_Query_Genre_Thriller);
        } else if (MoviesCategory.equals(getString(R.string.Western))) {
            resumeCode = 15;
            sortedBy = getString(R.string.API_Query_Genre_Western);
        }

        for (int i = 1; i < 6; i++) {

            String pageNum = Integer.toString(i);

            URL testURL = NetworkUtils.jsonRequest(sortedBy, pageNum);


            try {
                resultsString = new apiCall().execute(testURL).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<Movie> moviesAdd;
            moviesAdd = JsonUtils.parseApiResult(resultsString);

            for (Movie movie : moviesAdd) {

                for (FavEntry a : favorites) {
                    if (a.getName().equals(movie.getMovieName())) {
                        Log.d("T4", "YES");
                        movie.setFav(getString(R.string.Yes));
                    }
                }
                movies.add(movie);
            }
        }
    }

    public void setMoviesFavorites() {
        resumeCode = 3;
        movies = new ArrayList();
        String resultsString = "";


        for (FavEntry a : favorites) {
            String favID = a.getId() + "?";

            String movieIDQuery = getString(R.string.API_Query_Fav_Base) + favID + getString(R.string.API_key_append) + getString(R.string.API_key) + "&" + getString(R.string.API_Query_Videos_End);

            try {
                URL movieURL = new URL(movieIDQuery);
                resultsString = new apiCall().execute(movieURL).get();
                Movie movieAdd;

                movieAdd = JsonUtils.parseFavoriteMovie(resultsString);

                if (movieAdd != null) {
                    movieAdd.setFav(getString(R.string.Yes));
                    movies.add(movieAdd);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }


    public void onListItemClick(int clickedItemIndex) {
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
    }

    @Override
    public void onButtonClick(final int clickedItemIndex) {
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

    }


    public static class apiCall extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
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
            MainActivity.execute();
        }
    }
}
