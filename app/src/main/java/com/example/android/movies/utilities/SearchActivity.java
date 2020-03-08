package com.example.android.movies.utilities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

    public TextView tv;
    private String results;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_layout);

        results = "";

        tv = findViewById(R.id.test_text);

        handleSearch();
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

            Log.d("TEST", results);
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