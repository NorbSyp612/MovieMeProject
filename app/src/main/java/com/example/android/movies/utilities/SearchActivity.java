package com.example.android.movies.utilities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.android.movies.AppExecutors;
import com.example.android.movies.Items.Movie;
import com.example.android.movies.MainActivity;
import com.example.android.movies.MainViewModel;
import com.example.android.movies.R;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.example.android.movies.movieActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.ListItemClickListener, SearchAdapter.ButtonItemClickListener, OnAsyncFinished {

    private RecyclerView mRecycle;
    private ArrayList<Movie> movies;
    private SearchAdapter mAdapter;
    private FavEntry movieEntry;
    static List<FavEntry> favorites;
    private static ArrayList<Movie> favMovies = new ArrayList<>();
    private int position;
    private String instance_position;
    private String favorite;
    private AppDatabase mDb;
    private int clicks;
    private String instance_clicks;
    private View currentView;
    private static ArrayList<Movie> newMovies = new ArrayList<>();
    private int pageCount;
    private SwipeRefreshLayout swipeLayout;
    private String baseQuery;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_layout);

        mDb = AppDatabase.getInstance(getApplicationContext());

        clicks = 0;
        instance_clicks = "Clicks";
        pageCount = 1;
        String results = "";
        mRecycle = findViewById(R.id.search_results);

        swipeLayout = findViewById(R.id.search_refresh);
        swipeLayout.setRefreshing(true);


        if (savedInstanceState != null && savedInstanceState.containsKey(instance_clicks)) {
            clicks = savedInstanceState.getInt(instance_clicks, 0);
        }

        Context mContext = getApplicationContext();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecycle.setLayoutManager(layoutManager);

        handleSearch();

    }

    private void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavs().observe(this, new Observer<List<FavEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavEntry> favEntries) {
                Timber.d("onChanged viewModel");
                favorites = favEntries;

                favMovies.clear();

                ArrayList<String> genres = new ArrayList<>();
                ArrayList<String> ratings = new ArrayList<>();

                String ratingsTotal = "";

                for (FavEntry a : favorites) {
                    Movie addMovie = new Movie();
                    addMovie.setMovieName(a.getName());
                    favMovies.add(addMovie);
                    genres.add(a.getCategory());
                    ratings.add(a.getRating());
                    ratingsTotal = ratingsTotal + Double.parseDouble(a.getRating());
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        MainActivity.getFavs();
        super.onResume();
    }


    public void onFabClicked(View v) {
        ArrayList<Movie> mainMovies = MainActivity.getMovies();
        favMovies = MainActivity.getFavs();

        Context bContext = getApplicationContext();

        if (!mainMovies.isEmpty()) {


            if (favMovies.isEmpty()) {
                Toast.makeText(bContext, bContext.getString(R.string.AddFavPls), Toast.LENGTH_SHORT).show();
            } else if (favMovies.size() < 10) {
                Toast.makeText(bContext, bContext.getString(R.string.AddMoreThanTen), Toast.LENGTH_SHORT).show();
            } else {
                String movieIDQuery = "";

                ArrayList<String> result = movieMeProcessor.process(bContext);
                Timber.d(result.get(0));
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
                try {
                    String results = new apiCallButton().execute(testURL).get();
                    executeFavButton(results);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

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
            //   initiateFAB(mContext);
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

    public void goToMovieMeDetail(Movie movieMe) {
        clicks++;

        Class destination = movieActivity.class;

        Context mContext = getApplicationContext();

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
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Genre), movieMe.getGenre());
        goToMovieActivity.putExtra(mContext.getString(R.string.Is_Fav_Key), movieMe.getFav());

        mContext.startActivity(goToMovieActivity);
    }

    public void populateUI() {
        int numMovies = movies.size();
        mAdapter = new SearchAdapter(numMovies, this, this, movies, favMovies);
        mRecycle.setAdapter(mAdapter);
        mRecycle.setHasFixedSize(false);
        Timber.d("Setting position to %s", position);
        mRecycle.scrollToPosition(position);

        setupViewModel();

        mRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !swipeLayout.isRefreshing()) {
                    Log.d("T5", "onscrollstate");
                    swipeLayout.setRefreshing(true);
                    String page = Integer.toString(pageCount);
                    URL tester = NetworkUtils.jsonRequest(baseQuery, page);
                    new extra(SearchActivity.this, tester).execute();
                }
            }
        });


        swipeLayout.setRefreshing(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearch();
        super.onNewIntent(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        outState.putInt(instance_clicks, clicks);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void handleSearch() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            setTitle(searchQuery);
            baseQuery = getString(R.string.API_Search_Query_Base) + searchQuery + getString(R.string.API_Search_Query_End);
            String one = Integer.toString(pageCount);
            Log.d("T5", "Doing page count " + pageCount + " in handle search");
            pageCount++;
            URL testURL = NetworkUtils.jsonRequest(baseQuery, one);
            Log.d("T5", "First URL is " + testURL.toString());
            new search(this, testURL).execute();
        }
    }

    @Override
    public void onListItemClick(View itemView, int clickedItemIndex) {
        if (!movies.isEmpty()) {
            clicks++;
            currentView = itemView;
            position = clickedItemIndex - 1;
            Context context = SearchActivity.this;
            Class destination = movieActivity.class;

            int viewHolderPosition = clickedItemIndex;

            Timber.d("Viewholder position is: %s", viewHolderPosition);

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

    @Override
    public void onButtonClick(View itemView, final int clickedItemIndex) {

        clicks++;
        position = clickedItemIndex - 1;
        currentView = itemView;
        favorite = getString(R.string.No);


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


    }

    @Override
    public void onAsyncFinished(ArrayList<Movie> o) {

        if (!o.isEmpty()) {
            ArrayList<Movie> testArray = o;
            Movie testMovie = testArray.get(0);

            if (testMovie.getMovieName().equals("Key")) {
                Log.d("T5", "doing page: " + pageCount);
                pageCount++;
                testArray.remove(0);
                movies.addAll(testArray);
                mAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            } else {
                movies = o;
                populateUI();
            }
        }
    }

    static class search extends AsyncTask<URL, Void, ArrayList<Movie>> {

        private OnAsyncFinished onAsyncFinished;
        private ArrayList<Movie> moviesResult;
        private URL testURL;

        public search(OnAsyncFinished onAsyncFinished, URL url) {
            this.onAsyncFinished = onAsyncFinished;
            this.testURL = url;

        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            try {
                String apiResult = NetworkUtils.getResponseFromHttpUrl(this.testURL);
                moviesResult = JsonUtils.parseSearchResult(apiResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            onAsyncFinished.onAsyncFinished(movies);
        }
    }

    static class extra extends AsyncTask<URL, Void, ArrayList<Movie>> {
        private OnAsyncFinished onAsyncFinished;
        private ArrayList<Movie> moviesResult = new ArrayList<>();
        private URL testURL;

        public extra(OnAsyncFinished onAsyncFinished, URL url) {
            this.onAsyncFinished = onAsyncFinished;
            this.testURL = url;
            Movie t = new Movie();
            t.setMovieName("Key");
            moviesResult.add(t);
        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            try {
                String apiResult = NetworkUtils.getResponseFromHttpUrl(this.testURL);
                moviesResult.addAll(JsonUtils.parseSearchResult(apiResult));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            onAsyncFinished.onAsyncFinished(movies);
        }
    }

    class apiCallButton extends AsyncTask<URL, Void, String> {

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

        }
    }
}

