package com.example.android.movies.utilities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.MainActivity;
import com.example.android.movies.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_layout);

        tv = findViewById(R.id.test_text);

        // search
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
            Log.d("TEST", "handling search");
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            String letsgo = getString(R.string.API_Search_Query_Base) + searchQuery;
            String one = Integer.toString(1);
            URL testURL = NetworkUtils.jsonRequest(letsgo, one);
            Log.d("TEST", "test url is: " + testURL);
            String apiResult = "";

            try {
                apiResult = NetworkUtils.getResponseFromHttpUrl(testURL);
            } catch (IOException e) {
                e.printStackTrace();
            }

          //  ArrayList<Movie> queryResult = JsonUtils.parseApiResult(apiResult);
          //  tv.setText(queryResult.get(0).getMovieName());

        }else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            String selectedSuggestionRowId =  intent.getDataString();
            //execution comes here when an item is selected from search suggestions
            //you can continue from here with user selected search item
            Toast.makeText(this, "selected search suggestion "+selectedSuggestionRowId,
                    Toast.LENGTH_SHORT).show();
        }
    }
}