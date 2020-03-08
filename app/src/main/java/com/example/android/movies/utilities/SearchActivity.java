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

public class SearchActivity extends AppCompatActivity {

    public TextView tv;
    private RecyclerView mRecycle;
    private String results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_layout);

        results = "";

        tv = findViewById(R.id.test_text);
        mRecycle = findViewById(R.id.search_results);

        Context mContext = getApplicationContext();

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecycle.setLayoutManager(layoutManager);

        handleSearch();
    }

    public void onFabClicked(View v) {

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
            String letsgo = getString(R.string.API_Search_Query_Base) + searchQuery;
            String one = Integer.toString(1);
            URL testURL = NetworkUtils.jsonRequest(letsgo, one);
            try {
                results = new search().execute(testURL).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

class search extends AsyncTask<URL, String, String> {

    private String apiResult = "";

    @Override
    protected String doInBackground(URL... urls) {
        try {
            apiResult = NetworkUtils.getResponseFromHttpUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return apiResult;
    }

}