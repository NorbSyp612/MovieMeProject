package com.example.android.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class movieActivity extends AppCompatActivity {

    private TextView mName;
    private TextView mYear;
    private ImageView mPoster;
    private TextView mDate_Rating;
    private TextView mSynopsis;
    private TextView mRunTime;
    private ImageView mTrailer2TopBar;
    private ImageView mTrailer2PlayButton;
    private TextView mTrailer2Text;

    private ArrayList<String> movieTrailerURLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Context context = this;

        Intent fromMain = getIntent();


        mName = (TextView) findViewById(R.id.movie_title);
        mYear = (TextView) findViewById(R.id.movie_Year);
        mPoster = (ImageView) findViewById(R.id.movie_poster);
        mDate_Rating = (TextView) findViewById(R.id.movie_rating);
        mSynopsis = (TextView) findViewById(R.id.movie_summary);
        mRunTime = (TextView) findViewById(R.id.movie_RunTime);
        mTrailer2TopBar = (ImageView) findViewById(R.id.imageBar2);
        mTrailer2PlayButton = (ImageView) findViewById(R.id.movie_Play_Second_Trailer);
        mTrailer2Text = (TextView) findViewById(R.id.player_trailer_second_text);

        String movieName = this.getResources().getString(R.string.Movie_Name);
        String movieRunTime = "";

        String movieReleaseDate = this.getResources().getString(R.string.Movie_Release_Date);
        String movieReleaseDateString = fromMain.getStringExtra(movieReleaseDate);
        String movieYear = movieReleaseDateString.substring(0, 4);

        String movieRating = this.getResources().getString(R.string.Movie_Rating);
        String movieRatingString = fromMain.getStringExtra(movieRating);
        String movieRatingOutOfTen = movieRatingString + "/10";

        String movieSynopsis = this.getResources().getString(R.string.Movie_Synopsis);

        String movieIdURLString = fromMain.getStringExtra("MovieIdURL");
        String movieTrailerURLString = getString(R.string.API_Query_Videos_Start)
                + fromMain.getStringExtra(getString(R.string.Movie_ID))
                + getString(R.string.API_Query_Videos_Mid)
                + getString(R.string.API_key)
                + "&"
                + getString(R.string.API_Query_Videos_End);


        URL movieIdURL;
        URL movieTrailerURL;

        String movieIdDetailsResults = "";
        String movieTrailerResults = "";


        try {
            movieIdURL = new URL(movieIdURLString);
            movieTrailerURL = new URL(movieTrailerURLString);

            movieIdDetailsResults = new apiCallMovieID().execute(movieIdURL).get();
            movieTrailerResults = new apiCallMovieID().execute(movieTrailerURL).get();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (!movieIdDetailsResults.equals("")) {
            movieRunTime = JsonUtils.movieIDtest(movieIdDetailsResults);
            movieTrailerURLS = JsonUtils.getVideoLinks(movieTrailerResults);
        }


        mName.setText(fromMain.getStringExtra(movieName));
        mYear.setText(movieYear);
        mRunTime.setText(movieRunTime + "min");
        mDate_Rating.setText(movieRatingOutOfTen);
        mSynopsis.setText(fromMain.getStringExtra(movieSynopsis));

        String imgURL = getString(R.string.API_IMG_URL_BASE_342) + fromMain.getStringExtra(getString(R.string.Movie_Img_Url));
        Picasso.with(this).load(imgURL).into(mPoster);

        for (int i = 0; i < movieTrailerURLS.size(); i++) {
            Log.d("Movie", movieTrailerURLS.get(i));
            Log.d("Movie", String.valueOf(movieTrailerURLS.size()));
        }

        setSecondTrailerAssetsVisibility();
    }

    private void setSecondTrailerAssetsVisibility() {

        int i = movieTrailerURLS.size();

        if (i == 1) {
            mTrailer2TopBar.setVisibility(View.INVISIBLE);
            mTrailer2PlayButton.setVisibility(View.INVISIBLE);
            mTrailer2Text.setVisibility(View.INVISIBLE);
        } else {
            mTrailer2TopBar.setVisibility(View.VISIBLE);
            mTrailer2PlayButton.setVisibility(View.VISIBLE);
            mTrailer2Text.setVisibility(View.VISIBLE);
        }
    }

    public void First_Trailer_Button(View v) {

        String First_Trailer_URL = getString(R.string.Youtube_Base_URL) + movieTrailerURLS.get(0);
        Log.d("Test", First_Trailer_URL);

        Uri webpage = Uri.parse(First_Trailer_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    public void Second_Trailer_Button(View v) {

        String First_Trailer_URL = getString(R.string.Youtube_Base_URL) + movieTrailerURLS.get(1);

        Uri webpage = Uri.parse(First_Trailer_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    public static class apiCallMovieID extends AsyncTask<URL, Void, String> {

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
