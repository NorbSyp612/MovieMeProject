package com.example.android.movies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.OnAsyncFinished;
import com.example.android.movies.utilities.movieMeProcessor;
import com.example.android.movies.utilities.moviesAdapter;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import android.app.SearchManager;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE;

public class MainActivity extends AppCompatActivity implements moviesAdapter.ListItemClickListener, moviesAdapter.ButtonItemClickListener, NavigationView.OnNavigationItemSelectedListener, OnAsyncFinished {

    private static ArrayList<Movie> movies = new ArrayList<>();
    private static ArrayList<Movie> favMovies = new ArrayList<>();
    private static RecyclerView moviesGrid;
    private static moviesAdapter mAdapter;
    private SwipeRefreshLayout swipeLayout;
    private static final int NUM_LIST_MOVIES = 100;
    private static int currentMovieSizeTest;
    private AppDatabase mDb;
    static List<FavEntry> favorites;
    private static int resumeCode;
    private String INSTANCE_RESUME_CODE = "RESUME_CODE";
    private String INSTANCE_VIEW_POSITION_CODE = "POSITION CODE";
    private static String INSTANCE_CATEGORY;
    private static String current_Category;
    private static int NUM_LIST_MOVIES_FAVORITES;
    private String favorite;
    private FavEntry movieEntry;
    private static int asyncCount;
    private int buttonClick;
    private movieMeProcessor Processor;
    private int ExtraCounter;
    private ListView listView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private int pageNumber;
    private int extraTest;
    private boolean first;
    private boolean extra;

    private Context mContext;
    private static int statusCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.Most_Popular);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.MovieMe, R.string.MovieMe);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);


        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.containsKey(getString(R.string.GoToMovie))) {

            Context context = getApplicationContext();
            Class destination = movieActivity.class;

            Intent goToMovieActivity = new Intent(context, destination);

            goToMovieActivity.putExtra(context.getString(R.string.Movie_Name), extras.getString(getString(R.string.Movie_Name)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Img_Url), extras.getString(context.getString(R.string.Movie_Img_Url)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Synopsis), extras.getString(context.getString(R.string.Movie_Synopsis)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Rating), extras.getString(context.getString(R.string.Movie_Rating)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Release_Date), extras.getString(context.getString(R.string.Movie_Release_Date)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_ID_URL), extras.getString(context.getString(R.string.Movie_ID_URL)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_ID), extras.getString(context.getString(R.string.Movie_ID)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Backdrop), extras.getString(context.getString(R.string.Movie_Backdrop)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Genre), extras.getString(context.getString(R.string.Movie_Genre)));
            goToMovieActivity.putExtra(context.getString(R.string.Is_Fav_Key), context.getString(R.string.No));
            startActivity(goToMovieActivity);
        } else if (extras != null && extras.containsKey(getString(R.string.ID))) {
            Timber.d("FCM movie id is: %s", extras.getString(getString(R.string.ID)));
            String movieIDQuery = getString(R.string.API_Query_Fav_Base) + extras.getString(getString(R.string.ID)) + "?" + getString(R.string.API_key_append) + getString(R.string.API_key) + "&" + getString(R.string.API_Query_Videos_End);

            URL movieURL = null;
            try {
                movieURL = new URL(movieIDQuery);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new apiCallFCM(this).execute(movieURL);
        }

        extras = null;


        mContext = getApplicationContext();
        swipeLayout = findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (current_Category.equals(getString(R.string.Most_Popular))) {
                    setMoviesFromCategory(getString(R.string.Most_Popular));
                    setTitle(getString(R.string.Most_Popular));
                } else if (current_Category.equals(getString(R.string.Top_Rated))) {
                    setMoviesFromCategory(getString(R.string.Top_Rated));
                    setTitle(getString(R.string.Top_Rated));
                } else if (current_Category.equals(getString(R.string.Favorites))) {
                    // populateUIFavorites();
                    setMoviesFavorites();
                //    setTitle(getString(R.string.Favorites));
                //    moviesGrid.setVerticalScrollbarPosition(0);
                } else if (current_Category.equals(getString(R.string.Action))) {
                    setMoviesFromCategory(getString(R.string.Action));
                    setTitle(getString(R.string.Action));
                } else if (current_Category.equals(getString(R.string.Adventure))) {
                    setMoviesFromCategory(getString(R.string.Adventure));
                    setTitle(getString(R.string.Adventure));
                } else if (current_Category.equals(getString(R.string.Comedy))) {
                    setMoviesFromCategory(getString(R.string.Comedy));
                    setTitle(getString(R.string.Comedy));
                } else if (current_Category.equals(getString(R.string.History))) {
                    setMoviesFromCategory(getString(R.string.History));
                    setTitle(getString(R.string.History));
                } else if (current_Category.equals(getString(R.string.Horror))) {
                    setMoviesFromCategory(getString(R.string.Horror));
                    setTitle(getString(R.string.Horror));
                } else if (current_Category.equals(getString(R.string.Drama))) {
                    setMoviesFromCategory(getString(R.string.Drama));
                    setTitle(getString(R.string.Drama));
                } else if (current_Category.equals(getString(R.string.Fantasy))) {
                    setMoviesFromCategory(getString(R.string.Fantasy));
                    setTitle(getString(R.string.Fantasy));
                } else if (current_Category.equals(getString(R.string.Mystery))) {
                    setMoviesFromCategory(getString(R.string.Mystery));
                    setTitle(getString(R.string.Mystery));
                } else if (current_Category.equals(getString(R.string.Romance))) {
                    setMoviesFromCategory(getString(R.string.Romance));
                    setTitle(getString(R.string.Romance));
                } else if (current_Category.equals(getString(R.string.Science_Fiction))) {
                    setMoviesFromCategory(getString(R.string.Science_Fiction));
                    setTitle(getString(R.string.Science_Fiction));
                } else if (current_Category.equals(getString(R.string.Thriller))) {
                    setMoviesFromCategory(getString(R.string.Thriller));
                    setTitle(getString(R.string.Thriller));
                } else if (current_Category.equals(getString(R.string.Western))) {
                    setMoviesFromCategory(getString(R.string.Western));
                    setTitle(getString(R.string.Western));
                }
                swipeLayout.setRefreshing(false);
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_RESUME_CODE)) {
            resumeCode = savedInstanceState.getInt(INSTANCE_RESUME_CODE);
            current_Category = savedInstanceState.getString(INSTANCE_CATEGORY);
            Timber.d("Resume code: %s", resumeCode);
        } else {
            resumeCode = 1;
        }

        buttonClick = 0;
        statusCode = 0;


        int orientation = getResources().getConfiguration().orientation;
        int spanCount = 2;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4;
        }

        ExtraCounter = 1;

        moviesGrid = findViewById(R.id.movie_items);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount);
        moviesGrid.setLayoutManager(layoutManager);
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, this, movies, favMovies);

        moviesGrid.setAdapter(mAdapter);
        moviesGrid.setHasFixedSize(false);
        moviesGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!swipeLayout.isRefreshing() && !recyclerView.canScrollVertically(1) && !current_Category.equals(getString(R.string.Favorites))) {
                    moviesGrid.setHasFixedSize(false);
                    currentMovieSizeTest = movies.size() + 99;
                    mAdapter.setNumberMovies(movies.size() + 100);
                    setMoviesExtra(current_Category);
                }
            }
        });
        mDb = AppDatabase.getInstance(getApplicationContext());

        current_Category = getString(R.string.Most_Popular);

        setupViewModel();
        populateUI(current_Category);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_m).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_favs:
                if (!swipeLayout.isRefreshing()) {
                    if (favMovies.isEmpty()) {
                        Toast.makeText(this, getString(R.string.AddFavPls), Toast.LENGTH_SHORT).show();
                        swipeLayout.setRefreshing(false);
                    } else {
                        populateUIFavorites();
                        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Favorites);
                        current_Category = getString(R.string.Favorites);
                        break;
                    }
                }
            case R.id.drawer_pop:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Most_Popular));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Most_Popular);
                    current_Category = getString(R.string.Most_Popular);
                    break;
                }
            case R.id.drawer_top:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Top_Rated));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Top_Rated);
                    current_Category = getString(R.string.Top_Rated);
                    break;
                }
            case R.id.drawer_action:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Action));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Action);
                    current_Category = getString(R.string.Action);
                    break;
                }
            case R.id.drawer_adventure:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Adventure));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Adventure);
                    current_Category = getString(R.string.Adventure);
                    break;
                }
            case R.id.drawer_comedy:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Comedy));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Comedy);
                    current_Category = getString(R.string.Comedy);
                    break;
                }
            case R.id.drawer_History:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.History));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.History);
                    current_Category = getString(R.string.History);
                    break;
                }
            case R.id.drawer_horror:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Horror));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Horror);
                    current_Category = getString(R.string.Horror);
                    break;
                }
            case R.id.drawer_drama:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Drama));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Drama);
                    current_Category = getString(R.string.Drama);
                    break;
                }
            case R.id.drawer_fantasy:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Fantasy));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Fantasy);
                    current_Category = getString(R.string.Fantasy);
                    break;
                }
            case R.id.drawer_mystery:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Mystery));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Mystery);
                    current_Category = getString(R.string.Mystery);
                    break;
                }
            case R.id.drawer_romance:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Romance));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Romance);
                    current_Category = getString(R.string.Romance);
                    break;
                }
            case R.id.drawer_scifi:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Science_Fiction));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.SciFi);
                    current_Category = getString(R.string.SciFi);
                    break;
                }
            case R.id.drawer_thriller:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Thriller));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Thriller);
                    current_Category = getString(R.string.Thriller);
                    break;
                }
            case R.id.drawer_western:
                if (!swipeLayout.isRefreshing()) {
                    populateUI(getString(R.string.Western));
                    Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.Western);
                    current_Category = getString(R.string.Western);
                    break;
                }
            default:
                break;
        }
        drawerLayout.closeDrawers();
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_RESUME_CODE, resumeCode);
        outState.putString(INSTANCE_CATEGORY, current_Category);
        super.onSaveInstanceState(outState);
    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavs().observe(this, new Observer<List<FavEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavEntry> favEntries) {
                Timber.d("onChanged viewModel");
                favorites = favEntries;
                Processor = new movieMeProcessor(favorites);

                double numAction = 0;
                double numAdv = 0;
                double numComedy = 0;
                double numHistory = 0;
                double numHorror = 0;
                double numDrama = 0;
                double numFantasy = 0;
                double numMystery = 0;
                double numRomance = 0;
                double numSciFi = 0;
                double numThriller = 0;
                double numWestern = 0;

                String probAction = "";
                String probAdv = "";
                String probComedy = "";
                String probHistory = "";
                String probHorror = "";
                String probDrama = "";
                String probFantasy = "";
                String probMystery = "";
                String probRomance = "";
                String probScifi;
                String probThriller;
                String probWestern;

                double ratingsTotal = 0;

                favMovies.clear();

                ArrayList<String> genres = new ArrayList<>();
                ArrayList<String> ratings = new ArrayList<>();

                for (FavEntry a : favorites) {
                    Movie addMovie = new Movie();
                    addMovie.setMovieName(a.getName());
                    favMovies.add(addMovie);
                    genres.add(a.getCategory());
                    ratings.add(a.getRating());
                    ratingsTotal = ratingsTotal + Double.parseDouble(a.getRating());
                }


                for (String b : genres) {
                    if (b.equals(getString(R.string.Action))) {
                        numAction++;
                    } else if (b.equals(getString(R.string.Adventure))) {
                        numAdv++;
                    } else if (b.equals(getString(R.string.Comedy))) {
                        numComedy++;
                    } else if (b.equals(getString(R.string.History))) {
                        numHistory++;
                    } else if (b.equals(getString(R.string.Horror))) {
                        numHorror++;
                    } else if (b.equals(getString(R.string.Drama))) {
                        numDrama++;
                    } else if (b.equals(getString(R.string.Fantasy))) {
                        numFantasy++;
                    } else if (b.equals(getString(R.string.Mystery))) {
                        numMystery++;
                    } else if (b.equals(getString(R.string.Romance))) {
                        numRomance++;
                    } else if (b.equals(getString(R.string.SciFi))) {
                        numSciFi++;
                    } else if (b.equals(getString(R.string.Thriller))) {
                        numThriller++;
                    } else if (b.equals(getString(R.string.Western))) {
                        numWestern++;
                    }
                }


                int genreSize = genres.size() + 1;
                Timber.d("Genre size is : %s", String.valueOf(genreSize));
                ratingsTotal = ratingsTotal / genreSize;
                Timber.d("ratings total is: %s", ratingsTotal);
                String returnRatings = "" + ratingsTotal;
                Timber.d("return ratins is: %s", returnRatings);

                probAction = "" + (numAction / genreSize);
                probAdv = "" + (numAdv / genreSize);
                probComedy = "" + (numComedy / genreSize);
                probHistory = "" + (numHistory / genreSize);
                probHorror = "" + (numHorror / genreSize);
                probDrama = "" + (numDrama / genreSize);
                probFantasy = "" + (numFantasy / genreSize);
                probMystery = "" + (numMystery / genreSize);
                probRomance = "" + (numRomance / genreSize);
                probScifi = "" + (numSciFi / genreSize);
                probThriller = "" + (numThriller / genreSize);
                probWestern = "" + (numWestern / genreSize);


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(getString(R.string.Return_Ratings), returnRatings);
                editor.putString(getString(R.string.Action), probAction);
                editor.putString(getString(R.string.Adventure), probAdv);
                editor.putString(getString(R.string.Comedy), probComedy);
                editor.putString(getString(R.string.History), probHistory);
                editor.putString(getString(R.string.Horror), probHorror);
                editor.putString(getString(R.string.Drama), probDrama);
                editor.putString(getString(R.string.Fantasy), probFantasy);
                editor.putString(getString(R.string.Mystery), probMystery);
                editor.putString(getString(R.string.Romance), probRomance);
                editor.putString(getString(R.string.SciFi), probScifi);
                editor.putString(getString(R.string.Thriller), probThriller);
                editor.putString(getString(R.string.Western), probWestern);

                Timber.d(probAction);
                Timber.d(probAdv);

                editor.apply();

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public static ArrayList<Movie> getMovies() {
        return movies;
    }

    public static ArrayList<Movie> getFavs() {
        return favMovies;
    }

    @Override
    protected void onResume() {
        super.onResume();
        asyncCount = 0;
        buttonClick = 0;
    }


    public static int checkFav(Movie movie) {
        int check = 0;

        for (FavEntry a : favorites) {
            if (a.getId().equals(movie.getId())) {
                check = 1;
                break;
            }
        }

        return check;
    }

    public void populateUI(String category) {
        if (!swipeLayout.isRefreshing()) {
            Timber.d("populating UI from category");
            swipeLayout.setRefreshing(true);
            current_Category = category;
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
            swipeLayout.setRefreshing(false);
        }
    }

    public void populateUIFavorites() {
        if (!swipeLayout.isRefreshing()) {
            swipeLayout.setRefreshing(true);
            asyncCount = 0;
            current_Category = getString(R.string.Favorites);

            setMoviesFavorites();
        }
    }


    public void setMoviesExtra(String MoviesCategory) {
        resumeCode = 2;

        swipeLayout.setRefreshing(true);

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
            Timber.d(sortedBy);
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


        extraTest = movies.size() + 100;
        String pageNum = Integer.toString(pageNumber);
        URL testURL = NetworkUtils.jsonRequest(sortedBy, pageNum);
        new apiCallExtra(this, this, testURL).execute();

    }

    public void setMoviesFromCategory(String category) {

        Timber.d("setting movies from category");

        statusCode = 0;

        movies = new ArrayList<>();

        String sortedBy = "";

        if (category.equals(getString(R.string.Top_Rated))) {
            resumeCode = 2;
            sortedBy = getString(R.string.API_Query_TopRated_Desc);
        } else if (category.equals(getString(R.string.Most_Popular))) {
            resumeCode = 1;
            sortedBy = getString(R.string.API_Query_MostPop);
        } else if (category.equals(getString(R.string.Action))) {
            resumeCode = 4;
            sortedBy = getString(R.string.API_Query_Genre_Action) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Adventure))) {
            resumeCode = 5;
            sortedBy = getString(R.string.API_Query_Genre_Adventure) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Comedy))) {
            resumeCode = 6;
            sortedBy = getString(R.string.API_Query_Genre_Comedy) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.History))) {
            resumeCode = 7;
            sortedBy = getString(R.string.API_Query_Genre_History) + getString(R.string.API_Search_Part5);
            Timber.d(sortedBy);
        } else if (category.equals(getString(R.string.Horror))) {
            resumeCode = 8;
            sortedBy = getString(R.string.API_Query_Genre_Horror) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Drama))) {
            resumeCode = 9;
            sortedBy = getString(R.string.API_Query_Genre_Drama) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Fantasy))) {
            resumeCode = 10;
            sortedBy = getString(R.string.API_Query_Genre_Fantasy) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Mystery))) {
            resumeCode = 11;
            sortedBy = getString(R.string.API_Query_Genre_Mystery) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Romance))) {
            resumeCode = 12;
            sortedBy = getString(R.string.API_Query_Genre_Romance) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Science_Fiction))) {
            resumeCode = 13;
            sortedBy = getString(R.string.API_Query_Genre_Science_Fiction) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Thriller))) {
            resumeCode = 14;
            sortedBy = getString(R.string.API_Query_Genre_Thriller) + getString(R.string.API_Search_Part5);
        } else if (category.equals(getString(R.string.Western))) {
            resumeCode = 15;
            sortedBy = getString(R.string.API_Query_Genre_Western) + getString(R.string.API_Search_Part5);
        }


        pageNumber = 1;
        String pageNum = Integer.toString(pageNumber);
        URL testURL = NetworkUtils.jsonRequest(sortedBy, pageNum);
        new apiCall(this, this, testURL).execute();

    }

    public void setMoviesFavorites() {
        resumeCode = 3;
        movies = new ArrayList<>();

        if (favorites.isEmpty()) {
            Timber.d("is empty");
            setTitle(getString(R.string.Most_Popular));
            setMoviesFromCategory(getString(R.string.Most_Popular));
        } else {

            for (FavEntry a : favorites) {
                String favID = a.getId() + "?";

                String movieIDQuery = getString(R.string.API_Query_Fav_Base) + favID + getString(R.string.API_key_append) + getString(R.string.API_key) + "&" + getString(R.string.API_Query_Videos_End);

                Timber.d(movieIDQuery);

                URL movieURL = null;
                try {
                    movieURL = new URL(movieIDQuery);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                new apiCallFavs(this, this, movieURL).execute();
            }
        }

}

    public static void executeFCM(Movie firebaseMovie, Context context) {

        Context cContext = context;

        Intent goToMovieActivity = new Intent(cContext, movieActivity.class);

        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_Name), firebaseMovie.getMovieName());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_Img_Url), firebaseMovie.getImageURL());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_Synopsis), firebaseMovie.getSynopsis());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_Rating), firebaseMovie.getUserRating());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_Release_Date), firebaseMovie.getReleaseDate());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_ID_URL), firebaseMovie.getMovieIdURL());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_ID), firebaseMovie.getId());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_Backdrop), firebaseMovie.getBackdropURL());
        goToMovieActivity.putExtra(cContext.getString(R.string.Movie_Genre), firebaseMovie.getGenresString());

        String movieID = firebaseMovie.getId();
        String isFavorite = cContext.getString(R.string.No);

        for (FavEntry a : favorites) {
            if (a.getId().equals(movieID)) {
                isFavorite = cContext.getString(R.string.Yes);
                Timber.d("onListItemClick marking favorite as YES");
            }
        }

        goToMovieActivity.putExtra(cContext.getString(R.string.Is_Fav_Key), isFavorite);

        cContext.startActivity(goToMovieActivity);
    }

    public void onListItemClick(int clickedItemIndex) {

        if (!movies.isEmpty()) {
            Context context = MainActivity.this;
            Class destination = movieActivity.class;

            Timber.d("Viewholder position is: %s", clickedItemIndex);

            final Intent goToMovieActivity = new Intent(context, destination);

            goToMovieActivity.putExtra(getString(R.string.Movie_Name), movies.get(clickedItemIndex).getMovieName());
            goToMovieActivity.putExtra(getString(R.string.Movie_Img_Url), movies.get(clickedItemIndex).getImageURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_Synopsis), movies.get(clickedItemIndex).getSynopsis());
            goToMovieActivity.putExtra(getString(R.string.Movie_Rating), movies.get(clickedItemIndex).getUserRating());
            goToMovieActivity.putExtra(getString(R.string.Movie_Release_Date), movies.get(clickedItemIndex).getReleaseDate());
            goToMovieActivity.putExtra(getString(R.string.Movie_ID_URL), movies.get(clickedItemIndex).getMovieIdURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_ID), movies.get(clickedItemIndex).getId());
            goToMovieActivity.putExtra(getString(R.string.Movie_Backdrop), movies.get(clickedItemIndex).getBackdropURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_Genre), movies.get(clickedItemIndex).getGenresString());

            String movieID = movies.get(clickedItemIndex).getId();
            String isFavorite = getString(R.string.No);

            for (FavEntry a : favorites) {
                if (a.getId().equals(movieID)) {
                    isFavorite = getString(R.string.Yes);
                    Timber.d("onListItemClick marking favorite as YES");
                }
            }

            goToMovieActivity.putExtra(getString(R.string.Is_Fav_Key), isFavorite);

            startActivity(goToMovieActivity);
        } else {
            Timber.d("ERROR");
        }
    }

    public void goToMovieMeDetail(Movie movieMe) {
        Class destination = movieActivity.class;

        final Intent goToMovieActivity = new Intent(mContext, destination);

        goToMovieActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Name), movieMe.getMovieName());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Img_Url), movieMe.getImageURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Synopsis), movieMe.getSynopsis());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Rating), movieMe.getUserRating());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Release_Date), movieMe.getReleaseDate());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID_URL), movieMe.getMovieIdURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID), movieMe.getId());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Backdrop), movieMe.getBackdropURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Genre), movieMe.getGenresString());
        goToMovieActivity.putExtra(mContext.getString(R.string.Is_Fav_Key), mContext.getString(R.string.No));

        mContext.startActivity(goToMovieActivity);
    }

    @Override
    public void onButtonClick(final int clickedItemIndex) {

        if (!movies.isEmpty()) {

            favorite = getString(R.string.No);
            buttonClick = 1;

            for (FavEntry a : favorites) {
                if (a.getId().equals(movies.get(clickedItemIndex).getId())) {
                    favorite = getString(R.string.Yes);
                    movieEntry = a;
                    Timber.d("Movie is a favorite");
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
            Timber.d("ERROR");
        }

    }


    public void onFabClicked(View v) {
        initiateFAB(mContext);
    }

    public void initiateFAB(Context context) {
        Timber.d("FAB CLICKED");

        Context bContext = context;

        if (!movies.isEmpty()) {


            if (favorites.isEmpty()) {
                Toast.makeText(bContext, bContext.getString(R.string.AddFavPls), Toast.LENGTH_SHORT).show();
            } else if (favorites.size() < 10) {
                Toast.makeText(bContext, bContext.getString(R.string.AddMoreThanTen), Toast.LENGTH_SHORT).show();
            } else {
                String movieIDQuery = "";


                ArrayList<String> result = Processor.process(context);
                Timber.d("Scucess");
                Timber.d(result.get(0));
                statusCode = 1;
                Random rand = new Random();


                movieIDQuery = bContext.getString(R.string.API_Search_Part1) + bContext.getString(R.string.API_key) + bContext.getString(R.string.API_Search_Part2)
                        + (rand.nextInt(10) + 1) + bContext.getString(R.string.API_Search_Part3) + result.get(1) + bContext.getString(R.string.API_Search_Part4) + result.get(0)
                        + bContext.getString(R.string.API_Search_Part5);

                Timber.d(movieIDQuery);

                URL testURL = null;
                try {
                    testURL = new URL(movieIDQuery);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new apiCallButton(this).execute(testURL);

                Timber.d(movieIDQuery);
            }
        } else {
            Timber.d("ERROR");
        }
    }

    public void executeFavButton(String apiResults) {
        int favCheck = 0;
        Movie movieMe = new Movie();
        Random rand = new Random();


        ArrayList<Movie> movieMeResults;
        movieMeResults = JsonUtils.parseApiResult(apiResults);

        if (movieMeResults == null || movieMeResults.size() == 0) {
            initiateFAB(mContext);
        } else {
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
                    Timber.d(movieMe.getMovieName());
                } else {
                    Timber.d("Recommended a favorite starting over");
                }

            }

            if (movieMe.getMovieName() != null) {
                goToMovieMeDetail(movieMe);
            }
        }
    }

    public void executeFavs() {

        Timber.d("executing favs");
        mAdapter.setNumberMovies(favMovies.size());
        mAdapter.setMovies(movies);
        moviesGrid.setAdapter(mAdapter);
        swipeLayout.setRefreshing(false);

    }


    public void execute() {
        Timber.d("is empty: %s", favMovies.isEmpty());
        asyncCount = 0;
        mAdapter.setNumberMovies(NUM_LIST_MOVIES);
        mAdapter.setMovies(movies);
        moviesGrid.setAdapter(mAdapter);
        swipeLayout.setRefreshing(false);
    }

    public void executeExtra() {

        Timber.d("exeuctingExtra");
        asyncCount = 0;
        // mAdapter.addMovies(movies);
        mAdapter.notifyItemInserted(movies.size() - 99);
        mAdapter.notifyItemInserted(movies.size() - 98);
        mAdapter.notifyItemInserted(movies.size() - 97);
        mAdapter.notifyItemInserted(movies.size() - 96);
        mAdapter.notifyItemInserted(movies.size() - 95);
        mAdapter.notifyItemInserted(movies.size() - 94);
        mAdapter.notifyItemInserted(movies.size() - 93);
        mAdapter.notifyItemInserted(movies.size() - 92);
        mAdapter.notifyItemInserted(movies.size() - 91);
        mAdapter.notifyItemInserted(movies.size() - 90);
        swipeLayout.setRefreshing(false);
    }

    public static int getNumFavs() {
        return favMovies.size();
    }

    public static ArrayList<Movie> getFavMovies() {
        return favMovies;
    }

    @Override
    public void onAsyncFinished(ArrayList<Movie> o, String url) {


        if (url.equals("Key")) {
            executeFavs();
        } else {
            String result = "";
            pageNumber++;
            String pageNum = Integer.toString(pageNumber);
            int remove;

            if (pageNumber > 9) {
                remove = 2;
            } else {
                remove = 1;
            }

            if (url.length() > 0) {
                result = ((url.substring(0, url.length() - remove)) + pageNum);
            }

            if (first && o.size() < 100) {
                URL newUrl = NetworkUtils.jsonRequest(result, pageNum);
                try {
                    newUrl = new URL(result);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new apiCall(this, this, newUrl).execute();
            } else if (first) {
                execute();
                first = false;
            } else if (o.size() >= 100) {
                if (o.size() < extraTest) {
                    URL newUrl = NetworkUtils.jsonRequest(result, pageNum);
                    try {
                        newUrl = new URL(result);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    new apiCallExtra(this, this, newUrl).execute();
                } else {
                    executeExtra();
                }
            }


        }
    }


public static class apiCallButton extends AsyncTask<URL, Void, String> {

    private WeakReference<MainActivity> mainReference;

    public apiCallButton(MainActivity context) {
        mainReference = new WeakReference<>(context);
    }

    @Override
    protected String doInBackground(URL... urls) {
        Timber.d("doing in background");
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
            MainActivity activity = mainReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.executeFavButton(apiResults);
        }
    }
}


public static class apiCall extends AsyncTask<URL, Void, String> {

    private WeakReference<MainActivity> mainReference;
    private OnAsyncFinished onAsyncFinished;
    private URL link;
    String urlUsed;

    apiCall(MainActivity context, OnAsyncFinished onAsyncFinished, URL url) {
        mainReference = new WeakReference<>(context);
        this.onAsyncFinished = onAsyncFinished;
        this.link = url;
    }

    @Override
    protected String doInBackground(URL... urls) {
        String apiResult = null;

        //    Log.d("T7", "Trying URL: " + this.link.toString());

        try {
            apiResult = NetworkUtils.getResponseFromHttpUrl(this.link);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    @Override
    protected void onPostExecute(String apiResults) {

        MainActivity activity = mainReference.get();
        activity.first = true;

        if (apiResults == null) {
            Toast.makeText(activity.mContext, activity.mContext.getString(R.string.Error_Try_Again), Toast.LENGTH_SHORT).show();
        }


        ArrayList<Movie> moviesAdd;
        moviesAdd = JsonUtils.parseApiResult(apiResults);

        for (Movie movie : moviesAdd) {

            for (FavEntry a : favorites) {
                if (a.getName().equals(movie.getMovieName())) {
                    Timber.d("YES");
                    movie.setFav(activity.getString(R.string.Yes));
                }
            }
            movies.add(movie);
            //   Timber.d("movies size is: %s", movies.size());
        }

        this.onAsyncFinished.onAsyncFinished(movies, this.link.toString());
    }
}

public static class apiCallExtra extends AsyncTask<URL, Void, String> {

    private WeakReference<MainActivity> mainReference;
    private OnAsyncFinished onAsyncFinished;
    private URL link;

    apiCallExtra(MainActivity context, OnAsyncFinished on, URL url) {
        mainReference = new WeakReference<>(context);
        this.onAsyncFinished = on;
        this.link = url;
    }

    @Override
    protected String doInBackground(URL... urls) {
        String apiResult = null;

        try {
            apiResult = NetworkUtils.getResponseFromHttpUrl(this.link);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    @Override
    protected void onPostExecute(String apiResults) {

        MainActivity activity = mainReference.get();
        if (activity == null || activity.isFinishing()) return;

        if (apiResults == null) {
            Toast.makeText(activity.mContext, activity.mContext.getString(R.string.Error_Try_Again), Toast.LENGTH_SHORT).show();
        }

        ArrayList<Movie> moviesAdd;
        moviesAdd = JsonUtils.parseApiResult(apiResults);


        for (Movie movie : moviesAdd) {

            for (FavEntry a : favorites) {
                if (a.getName().equals(movie.getMovieName())) {
                    Timber.d("YES");
                    movie.setFav(activity.mContext.getString(R.string.Yes));
                }
            }
            movies.add(movie);
        }


        this.onAsyncFinished.onAsyncFinished(movies, this.link.toString());


    }
}

public static class apiCallFCM extends AsyncTask<URL, Void, String> {

    private WeakReference<MainActivity> mainReference;

    apiCallFCM(MainActivity context) {
        mainReference = new WeakReference<>(context);
    }

    @Override
    protected String doInBackground(URL... urls) {
        Timber.d("doing in background");
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

        MainActivity activity = mainReference.get();
        if (activity == null || activity.isFinishing()) return;

        Movie addMovie = JsonUtils.parseFavoriteMovie(apiResults);
        MainActivity.executeFCM(addMovie, activity.mContext);
    }
}

public static class apiCallFavs extends AsyncTask<URL, Void, String> {

    private WeakReference<MainActivity> mainReference;
    private OnAsyncFinished onAsyncFinished;
    private URL link;

    apiCallFavs(MainActivity context, OnAsyncFinished on, URL url) {
        mainReference = new WeakReference<>(context);
        this.onAsyncFinished = on;
        this.link = url;
        Log.d("T9", this.link.toString());
    }

    @Override
    protected String doInBackground(URL... urls) {
        String apiResult = null;

        try {
            apiResult = NetworkUtils.getResponseFromHttpUrl(this.link);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return apiResult;
    }

    @Override
    protected void onPostExecute(String apiResults) {

        Movie addMovie = JsonUtils.parseFavoriteMovie(apiResults);

        if (addMovie != null) {

            MainActivity activity = mainReference.get();
            if (activity == null || activity.isFinishing()) return;

            addMovie.setFav(activity.mContext.getString(R.string.Yes));
            movies.add(addMovie);
        }

        if (movies.size() == favMovies.size()) {
            this.onAsyncFinished.onAsyncFinished(movies, "Key");
        }

    }
}
}

