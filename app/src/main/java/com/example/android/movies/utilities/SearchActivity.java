package com.example.android.movies.utilities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.movies.R;


public class SearchActivity extends AppCompatActivity {
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_layout);

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override public void onItemClick(AdapterView<?> parent, View view,
                                              int position, long id) {
                //execution come here when an item is clicked from
                //the search results displayed after search form is submitted
                //you can continue from here with user clicked search item
                Toast.makeText(SearchActivity.this,
                        "clicked search result item is"+((TextView)view).getText(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        // search
        handleSearch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSearch();
    }
    private void handleSearch() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);

            SearchAdapter adapter = new SearchAdapter(this,
                    android.R.layout.simple_dropdown_item_1line,
                    JsonUtils.parseApiResult(searchQuery));
            listView.setAdapter(adapter);

        }else if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            String selectedSuggestionRowId =  intent.getDataString();
            //execution comes here when an item is selected from search suggestions
            //you can continue from here with user selected search item
            Toast.makeText(this, "selected search suggestion "+selectedSuggestionRowId,
                    Toast.LENGTH_SHORT).show();
        }
    }
}