package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.moviesAdapter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements moviesAdapter.ListItemClickListener {

    private ArrayList<Movie> movies = new ArrayList<Movie>();
    private RecyclerView moviesGrid;
    private moviesAdapter mAdapter;
    private int menuCounter;
    private static final int NUM_LIST_MOVIES = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;

        setMoviesMostPop();
        setTitle(R.string.Most_Popular);

        menuCounter = 2;

        moviesGrid = (RecyclerView) findViewById(R.id.movie_items);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        moviesGrid.setLayoutManager(layoutManager);
        mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, movies);
        moviesGrid.setAdapter(mAdapter);
        moviesGrid.setHasFixedSize(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId) {

            case R.id.sort_view:

                if (menuCounter % 2 == 0) {
                    setMoviesTopRated();
                    mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, movies);
                    moviesGrid.setAdapter(mAdapter);
                    setTitle(R.string.Top_Rated);
                    menuCounter++;
                    return true;
                } else {
                    setMoviesMostPop();
                    mAdapter = new moviesAdapter(NUM_LIST_MOVIES, this, movies);
                    moviesGrid.setAdapter(mAdapter);
                    setTitle(R.string.Most_Popular);
                    menuCounter++;
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMoviesMostPop() {

        movies = new ArrayList<Movie>();

        String resultsString = "";

        for (int i = 1; i < 6; i++) {

            String pageNum = Integer.toString(i);

            URL testURL = NetworkUtils.jsonRequest(getString(R.string.API_Query_MostPop), pageNum);


            try {
                resultsString = new apiCall().execute(testURL).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<Movie> moviesAdd = new ArrayList<Movie>();
            moviesAdd = JsonUtils.parseApiResult(resultsString);

            for (Movie movie : moviesAdd) {
                movies.add(movie);
            }
        }
    }

    public void setMoviesTopRated() {
        String resultsString = "";

        movies = new ArrayList<Movie>();

        for (int i = 1; i < 6; i++) {

            String pageNum = Integer.toString(i);

            URL testURL = NetworkUtils.jsonRequest(getString(R.string.API_Query_TopRated_Desc), pageNum);


            try {
                resultsString = new apiCall().execute(testURL).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<Movie> moviesAdd = new ArrayList<Movie>();
            moviesAdd = JsonUtils.parseApiResult(resultsString);

            for (Movie movie : moviesAdd) {
                movies.add(movie);
            }
        }
    }

    public void onListItemClick(int clickedItemIndex) {
        Context context = MainActivity.this;
        Class destination = movieActivity.class;

        Intent goToMovieActivity = new Intent(context, destination);

        goToMovieActivity.putExtra(getString(R.string.Movie_Name), movies.get(clickedItemIndex).getMovieName());
        goToMovieActivity.putExtra(getString(R.string.Movie_Img_Url), movies.get(clickedItemIndex).getImageURL());
        goToMovieActivity.putExtra(getString(R.string.Movie_Synopsis), movies.get(clickedItemIndex).getSynopsis());
        goToMovieActivity.putExtra(getString(R.string.Movie_Rating), movies.get(clickedItemIndex).getUserRating());
        goToMovieActivity.putExtra(getString(R.string.Movie_Release_Date), movies.get(clickedItemIndex).getReleaseDate());
        goToMovieActivity.putExtra(getString(R.string.Movie_ID_URL), movies.get(clickedItemIndex).getMovieIdURL());
        goToMovieActivity.putExtra(getString(R.string.Movie_ID), movies.get(clickedItemIndex).getId());

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
