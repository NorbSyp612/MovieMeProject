package com.example.android.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements moviesAdapter.ListItemClickListener {

    private ArrayList<Movie> movies = new ArrayList();
    private RecyclerView moviesGrid;
    private moviesAdapter mAdapter;
    private static final int NUM_LIST_MOVIES = 100;
    private String isFavorite;
    private AppDatabase mDb;
    List<FavEntry> favorites;
    private int resumeCode;
    private String movieID;
    private String INSTANCE_RESUME_CODE = "RESUME_CODE";
    private String INSTANCE_VIEW_POSITION_CODE = "POSITION CODE";
    private int viewHolderPosition;
    int NUM_LIST_MOVIES_FAVORITES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_RESUME_CODE)) {
            resumeCode = savedInstanceState.getInt(INSTANCE_RESUME_CODE);
            viewHolderPosition = savedInstanceState.getInt(INSTANCE_VIEW_POSITION_CODE);
        } else {
            resumeCode = 1;
        }


        moviesGrid = (RecyclerView) findViewById(R.id.movie_items);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        moviesGrid.setLayoutManager(layoutManager);
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, movies);

        moviesGrid.setAdapter(mAdapter);
        moviesGrid.setHasFixedSize(true);
        mDb = AppDatabase.getInstance(getApplicationContext());


        if (resumeCode == 0) {
            setMoviesFromCategory(getString(R.string.Most_Popular));
        }

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
                favorites = favEntries;

                if (resumeCode == 3) {
                    populateUIFavorites();
                    scrollToPosition();
                } else if (resumeCode == 2) {
                    populateUITopRated();
                    scrollToPosition();
                } else if (resumeCode == 1) {
                    populateUIMostPop();
                    scrollToPosition();
                } else {
                    populateUI(getString(R.string.Most_Popular));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void populateUIMostPop() {
        setMoviesFromCategory(getString(R.string.Most_Popular));
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, movies);
        moviesGrid.setAdapter(mAdapter);
        setTitle(R.string.Most_Popular);
    }

    public void populateUITopRated() {
        setMoviesFromCategory("Top");
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, movies);
        moviesGrid.setAdapter(mAdapter);
        setTitle(R.string.Top_Rated);
    }

    public void populateUI(String category) {

        if (category.equals(getString(R.string.Most_Popular))) {
            setMoviesFromCategory(getString(R.string.Most_Popular));
            setTitle(getString(R.string.Most_Popular));
        }
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, movies);
        moviesGrid.setAdapter(mAdapter);

    }

    public void populateUIFavorites() {
        setMoviesFavorites();
        NUM_LIST_MOVIES_FAVORITES = movies.size();
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES_FAVORITES, this, movies);
        moviesGrid.setAdapter(mAdapter);
        setTitle(R.string.Set_Title_Favorite);
    }

    private void scrollToPosition() {
        if (resumeCode > 0) {
            if (resumeCode == 3 && viewHolderPosition == NUM_LIST_MOVIES_FAVORITES) {
                viewHolderPosition--;
            }
            moviesGrid.scrollToPosition(viewHolderPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.sort_pop:
                populateUIMostPop();
                return true;

            case R.id.sort_rated:
                populateUITopRated();
                return true;

            case R.id.sort_favorites:
                populateUIFavorites();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMoviesFromCategory(String category) {
        resumeCode = 1;

        movies = new ArrayList();

        String resultsString = "";
        String MoviesCategory = category;
        String sortedBy = "";

        if (MoviesCategory.equals("Top")) {
            sortedBy = getString(R.string.API_Query_TopRated_Desc);
        } else if (MoviesCategory.equals(getString(R.string.Most_Popular))) {
            sortedBy = getString(R.string.API_Query_MostPop);
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

        final Intent goToMovieActivity = new Intent(context, destination);

        goToMovieActivity.putExtra(getString(R.string.Movie_Name), movies.get(clickedItemIndex).getMovieName());
        goToMovieActivity.putExtra(getString(R.string.Movie_Img_Url), movies.get(clickedItemIndex).getImageURL());
        goToMovieActivity.putExtra(getString(R.string.Movie_Synopsis), movies.get(clickedItemIndex).getSynopsis());
        goToMovieActivity.putExtra(getString(R.string.Movie_Rating), movies.get(clickedItemIndex).getUserRating());
        goToMovieActivity.putExtra(getString(R.string.Movie_Release_Date), movies.get(clickedItemIndex).getReleaseDate());
        goToMovieActivity.putExtra(getString(R.string.Movie_ID_URL), movies.get(clickedItemIndex).getMovieIdURL());
        goToMovieActivity.putExtra(getString(R.string.Movie_ID), movies.get(clickedItemIndex).getId());

        movieID = movies.get(clickedItemIndex).getId();
        isFavorite = getString(R.string.No);

        for (FavEntry a : favorites) {
            if (a.getId().equals(movieID)) {
                isFavorite = getString(R.string.Yes);
            }
        }

        goToMovieActivity.putExtra(getString(R.string.Is_Fav_Key), isFavorite);

        startActivity(goToMovieActivity);
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

        }
    }
}
