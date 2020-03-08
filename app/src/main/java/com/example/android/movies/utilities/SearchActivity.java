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

import java.io.IOException;
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

    public void onFabClicked(View v) {

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