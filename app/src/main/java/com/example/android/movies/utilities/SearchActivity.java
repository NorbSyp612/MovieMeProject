package com.example.android.movies.utilities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.MainActivity;
import com.example.android.movies.R;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.example.android.movies.movieActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.ListItemClickListener {

    private RecyclerView mRecycle;
    private ArrayList<Movie> movies;
    private String results;
    private SearchAdapter mAdapter;
    private static ArrayList<Movie> mainMovies = new ArrayList<>();
    private static ArrayList<Movie> favMovies = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_layout);

        results = "";
        mRecycle = findViewById(R.id.search_results);

        Context mContext = getApplicationContext();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecycle.setLayoutManager(layoutManager);

        handleSearch();
    }

    @Override
    protected void onResume() {
        MainActivity.getFavs();
        super.onResume();
    }

    public void onFabClicked(View v) {
        mainMovies = MainActivity.getMovies();
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
        goToMovieActivity.putExtra(mContext.getString(R.string.Is_Fav_Key), mContext.getString(R.string.No));

        mContext.startActivity(goToMovieActivity);
    }

    public void populateUI() {
        int numMovies = movies.size();
        mAdapter = new SearchAdapter(numMovies, this, movies);
        mRecycle.setAdapter(mAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearch();
        super.onNewIntent(intent);
    }

    private void handleSearch() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            setTitle(searchQuery);
            String letsgo = getString(R.string.API_Search_Query_Base) + searchQuery + getString(R.string.API_Search_Query_End);
            String one = Integer.toString(1);
            URL testURL = NetworkUtils.jsonRequest(letsgo, one);
            try {
                movies = new search().execute(testURL).get();
                populateUI();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (!movies.isEmpty()) {
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

            int check = MainActivity.checkFav(movies.get(clickedItemIndex));

            if (check == 1) {
                isFavorite = getString(R.string.Yes);
            }

            goToMovieActivity.putExtra(getString(R.string.Is_Fav_Key), isFavorite);

            startActivity(goToMovieActivity);
        } else {
            Timber.d("ERROR");
        }
    }
}

class search extends AsyncTask<URL, Void, ArrayList<Movie>> {

    private ArrayList<Movie> moviesResult;

    @Override
    protected ArrayList<Movie> doInBackground(URL... urls) {
        try {
            String apiResult = NetworkUtils.getResponseFromHttpUrl(urls[0]);
            moviesResult = JsonUtils.parseSearchResult(apiResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moviesResult;
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