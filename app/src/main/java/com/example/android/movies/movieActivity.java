package com.example.android.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
    private View mReviewSection;
    private TextView mFirstReview;
    private TextView mSecondreview;
    private ImageView mReviewSeparator;
    private ImageView mTrailerBottomBar;
    private TextView mTrailerText;
    private ImageView mTrailer1PlayButton;
    private TextView mTrailer1Text;
    private ImageView mTopImageBar;
    private AppDatabase mDb;
    private String mMovieName;
    private String mMovieID;
    private Button mFavButton;
    private String favorite;
    private FavEntry movieEntry;

    public static final String INSTANCE_MOVIE_ID = "MovieId";
    private static final String INSTANCE_FAV = "InstanceFAV";

    String movieRunTime;
    private ArrayList<String> movieTrailerURLS;
    private ArrayList<String> movieReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent fromMain = getIntent();

        mDb = AppDatabase.getInstance(getApplicationContext());

        intiViews();

        String movieName = this.getResources().getString(R.string.Movie_Name);

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

        String movieReviewURLString = getString(R.string.API_Query_Videos_Start)
                + fromMain.getStringExtra(getString(R.string.Movie_ID))
                + getString(R.string.API_Query_Reviews_Mid)
                + getString(R.string.API_key)
                + "&"
                + getString(R.string.API_Query_Videos_End);


        URL movieIdURL;
        URL movieTrailerURL;
        URL movieReviewURL;

        String movieIdDetailsResults = "";
        String movieTrailerResults = "";
        String movieReviewResults = "";


        try {
            movieIdURL = new URL(movieIdURLString);
            movieTrailerURL = new URL(movieTrailerURLString);
            movieReviewURL = new URL(movieReviewURLString);

            movieIdDetailsResults = new apiCallMovieID().execute(movieIdURL).get();
            movieTrailerResults = new apiCallMovieID().execute(movieTrailerURL).get();
            movieReviewResults = new apiCallMovieID().execute(movieReviewURL).get();

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
            movieReviews = JsonUtils.getReviews(movieReviewResults);
        }

        String runTime = movieRunTime + getString(R.string.Min);
        mMovieName = fromMain.getStringExtra(movieName);

        mName.setText(fromMain.getStringExtra(movieName));
        mYear.setText(movieYear);
        mRunTime.setText(runTime);
        mDate_Rating.setText(movieRatingOutOfTen);
        mSynopsis.setText(fromMain.getStringExtra(movieSynopsis));

        String imgURL = getString(R.string.API_IMG_URL_BASE_342) + fromMain.getStringExtra(getString(R.string.Movie_Img_Url));
        Picasso.with(this).load(imgURL).into(mPoster);

        mTrailerBottomBar.setVisibility(View.GONE);
        setTrailersVisibilityAndContent();
        setReviewVisibilityAndContent();

        mMovieID = fromMain.getStringExtra(getString(R.string.Movie_ID));

        favorite = fromMain.getStringExtra(getString(R.string.Is_Fav_Key));

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FAV)) {
            favorite = savedInstanceState.getString(INSTANCE_FAV);
        }
        setupViewModel();
    }

    private void setupViewModel() {
        AddFavViewModelFactory factory = new AddFavViewModelFactory(mDb, mMovieID);
        AddFavViewModel viewModel = ViewModelProviders.of(this, factory).get(AddFavViewModel.class);
        viewModel.getFav().observe(this, new Observer<FavEntry>() {
            @Override
            public void onChanged(@Nullable FavEntry favEntry) {
                setFavButton();
                movieEntry = favEntry;
            }
        });
    }

    private void intiViews() {
        mFavButton = (Button) findViewById(R.id.button);
        mName = (TextView) findViewById(R.id.movie_title);
        mYear = (TextView) findViewById(R.id.movie_Year);
        mPoster = (ImageView) findViewById(R.id.movie_poster);
        mDate_Rating = (TextView) findViewById(R.id.movie_rating);
        mSynopsis = (TextView) findViewById(R.id.movie_summary);
        mRunTime = (TextView) findViewById(R.id.movie_RunTime);
        mTrailer2TopBar = (ImageView) findViewById(R.id.imageBar2);
        mTrailer2PlayButton = (ImageView) findViewById(R.id.movie_Play_Second_Trailer);
        mTrailer2Text = (TextView) findViewById(R.id.player_trailer_second_text);
        mReviewSection = (View) findViewById(R.id.include);
        mFirstReview = (TextView) findViewById(R.id.movie_first_review);
        mSecondreview = (TextView) findViewById(R.id.movie_second_review);
        mReviewSeparator = (ImageView) findViewById(R.id.imageBar_seperator);
        mTrailerBottomBar = (ImageView) findViewById(R.id.imageBar3);
        mTrailerText = (TextView) findViewById(R.id.textView);
        mTrailer1PlayButton = (ImageView) findViewById(R.id.movie_Play_First_Trailer);
        mTrailer1Text = (TextView) findViewById(R.id.play_trailer_first_text);
        mTopImageBar = (ImageView) findViewById(R.id.imageBar1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(INSTANCE_FAV, favorite);
        outState.putString(INSTANCE_MOVIE_ID, mMovieID);
        super.onSaveInstanceState(outState);
    }


    private void setFavButton() {
        if (favorite.equals(getString(R.string.Yes))) {
            mFavButton.setText(R.string.Remove_Fav);
        } else {
            mFavButton.setText(R.string.Mark_Fav);
        }
    }

    private void setTrailersVisibilityAndContent() {

        int i = movieTrailerURLS.size();

        if (i == 0) {
            mTopImageBar.setVisibility(View.GONE);
            mTrailer1Text.setVisibility(View.GONE);
            mTrailerText.setVisibility(View.GONE);
            mTrailer1PlayButton.setVisibility(View.GONE);
            mTrailer2TopBar.setVisibility(View.GONE);
            mTrailer2PlayButton.setVisibility(View.GONE);
            mTrailer2Text.setVisibility(View.GONE);
        } else if (i == 1) {
            mTrailer2TopBar.setVisibility(View.GONE);
            mTrailer2PlayButton.setVisibility(View.GONE);
            mTrailer2Text.setVisibility(View.GONE);
        } else {
            mTrailer1Text.setVisibility(View.VISIBLE);
            mTrailerText.setVisibility(View.VISIBLE);
            mTrailer1PlayButton.setVisibility(View.VISIBLE);
            mTrailer2TopBar.setVisibility(View.VISIBLE);
            mTrailer2PlayButton.setVisibility(View.VISIBLE);
            mTrailer2Text.setVisibility(View.VISIBLE);
        }
    }

    private void setReviewVisibilityAndContent() {

        int i = movieReviews.size();
        mReviewSeparator.setVisibility(View.INVISIBLE);

        if (movieReviews.isEmpty()) {
            mReviewSection.setVisibility(View.GONE);
        } else {
            mTrailerBottomBar.setVisibility(View.VISIBLE);
            mReviewSection.setVisibility(View.VISIBLE);
            mFirstReview.setText(movieReviews.get(0));
            if (i == 2) {
                mReviewSeparator.setVisibility(View.VISIBLE);
                mSecondreview.setText(movieReviews.get(1));
            }
        }
    }

    public void onFavoriteClicked(View v) {

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (favorite.equals(getString(R.string.Yes))) {
                    mDb.favDao().deleteFav(movieEntry);
                    favorite=getString(R.string.No);
                } else {
                    FavEntry enterNewFavorite = new FavEntry(mMovieID, mMovieName);
                    mDb.favDao().insertFav(enterNewFavorite);
                    favorite=getString(R.string.Yes);
                }
            }
        });

    }

    public void First_Trailer_Button(View v) {

        String First_Trailer_URL = getString(R.string.Youtube_Base_URL) + movieTrailerURLS.get(0);

        Uri webpage = Uri.parse(First_Trailer_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    public void Second_Trailer_Button(View v) {

        String Second_Trailer_URL = getString(R.string.Youtube_Base_URL) + movieTrailerURLS.get(1);

        Uri webpage = Uri.parse(Second_Trailer_URL);
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
