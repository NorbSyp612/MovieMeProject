package com.example.android.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.example.android.movies.utilities.JsonUtils;
import com.example.android.movies.utilities.NetworkUtils;
import com.example.android.movies.utilities.movieMeProcessor;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class movieActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private TextView mDate_Rating;
    private TextView mSynopsis;
    private View mReviewSection;
    private TextView mFirstReview;
    private TextView mSecondreview;
    private ImageView mReviewSeparator;
    private ImageView mTrailerBottomBar;
    private TextView mTrailerText;
    private ImageView mTopImageBar;
    private AppDatabase mDb;
    private String mMovieName;
    private String mMovieID;
    private String mMovieGenre;
    private String mMovieRating;
    private ImageView mFavButtonBackground;
    private String favorite;
    private FavEntry movieEntry;
    private ImageView mToolbarPoster;
    private CollapsingToolbarLayout mCollapseLayout;
    private YouTubePlayerFragment playerFragment;
    private YouTubePlayer mPlayer;
    AddFavViewModel viewModel;
    AddFavViewModelFactory factory;
    private int buttonPressed;

    public static final String INSTANCE_MOVIE_ID = "MovieId";
    private static final String INSTANCE_FAV = "InstanceFAV";

    String movieRunTime;
    private ArrayList<String> movieTrailerURLS;
    private ArrayList<String> movieReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Log.d("T9", "On Create");

        Intent fromMain = getIntent();

        if (savedInstanceState != null) {
            buttonPressed = savedInstanceState.getInt("TESTER");
        }


        mDb = AppDatabase.getInstance(getApplicationContext());

        intiViews();

        String movieName = this.getResources().getString(R.string.Movie_Name);
        String movieRating = this.getResources().getString(R.string.Movie_Rating);
        String movieRatingString = fromMain.getStringExtra(movieRating);
        String movieRatingOutOfTen = movieRatingString + this.getResources().getString(R.string.Out_of_ten);

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


        mMovieName = fromMain.getStringExtra(movieName);
        mMovieGenre = fromMain.getStringExtra(getResources().getString(R.string.Movie_Genre));
        mMovieRating = fromMain.getStringExtra(getResources().getString(R.string.Movie_Rating));

        mDate_Rating.setText(movieRatingOutOfTen);
        mSynopsis.setText(fromMain.getStringExtra(movieSynopsis));

        String backdropImgURL = getString(R.string.API_IMG_URL_BASE_342) + fromMain.getStringExtra(getString(R.string.Movie_Backdrop));
        Picasso.with(this).load(backdropImgURL).into(mToolbarPoster);

        mTrailerBottomBar.setVisibility(View.GONE);
        setTrailersVisibilityAndContent();
        setReviewVisibilityAndContent();

        mMovieID = fromMain.getStringExtra(getString(R.string.Movie_ID));

        favorite = fromMain.getStringExtra(getString(R.string.Is_Fav_Key));

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FAV)) {
            favorite = savedInstanceState.getString(INSTANCE_FAV);
        }
        setupViewModel();

        mCollapseLayout.setTitle(fromMain.getStringExtra(movieName));

        playerFragment.initialize(getString(R.string.Youtube_API_Key), this);

    }

    public void setRunTimeTrailerReviews(Movie movieMe) {

        String movieTrailerURLString = getString(R.string.API_Query_Videos_Start)
                + movieMe.getId()
                + getString(R.string.API_Query_Videos_Mid)
                + getString(R.string.API_key)
                + "&"
                + getString(R.string.API_Query_Videos_End);

        String movieReviewURLString = getString(R.string.API_Query_Videos_Start)
                + movieMe.getId()
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
            movieIdURL = new URL(movieMe.getMovieIdURL());
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TEST6", "Destroy");
        mPlayer = null;
    }

    private void setupViewModel() {

        factory = new AddFavViewModelFactory(mDb, mMovieID);

        viewModel = ViewModelProviders.of(this, factory).get(AddFavViewModel.class);
        viewModel.getFav().observe(this, new Observer<FavEntry>() {
            @Override
            public void onChanged(@Nullable FavEntry favEntry) {
                setFavButton();
                movieEntry = favEntry;
                Log.d("T9", "liveData onChange");
            }
        });

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        mPlayer = player;
        mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        if (!wasRestored) {
            if (!movieTrailerURLS.isEmpty()) {
                player.cueVideo(movieTrailerURLS.get(0));
            } else {
                playerFragment.getView().setVisibility(View.GONE);
            }
        } else {

        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        mPlayer = null;
    }


    private void intiViews() {
        mFavButtonBackground = (ImageView) findViewById(R.id.button_background);
        mDate_Rating = (TextView) findViewById(R.id.movie_rating);
        mSynopsis = (TextView) findViewById(R.id.movie_summary);
        mReviewSection = (View) findViewById(R.id.include);
        mFirstReview = (TextView) findViewById(R.id.movie_first_review);
        mSecondreview = (TextView) findViewById(R.id.movie_second_review);
        mReviewSeparator = (ImageView) findViewById(R.id.imageBar_seperator);
        mTrailerBottomBar = (ImageView) findViewById(R.id.imageBar3);
        mTrailerText = (TextView) findViewById(R.id.textView);
        mTopImageBar = (ImageView) findViewById(R.id.imageBar1);
        mToolbarPoster = (ImageView) findViewById(R.id.movie_toolbar_poster);
        mCollapseLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.movie_Play_First_Trailer);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(INSTANCE_FAV, favorite);
        outState.putString(INSTANCE_MOVIE_ID, mMovieID);
        outState.putInt("TESTER", buttonPressed);
        Log.d("T9", "OUT STATE: " + mMovieID);
        super.onSaveInstanceState(outState);
    }


    private void setFavButton() {
        if (favorite.equals(getString(R.string.Yes))) {
            mFavButtonBackground.setImageDrawable(getDrawable(R.drawable.ic_star_yellow));
        } else {
            mFavButtonBackground.setImageDrawable(getDrawable(R.drawable.ic_star_background_24dp));
        }
    }

    private void setTrailersVisibilityAndContent() {
        int i = movieTrailerURLS.size();
        if (i == 0) {
            mTopImageBar.setVisibility(View.GONE);
            mTrailerText.setVisibility(View.GONE);
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
                    favorite = getString(R.string.No);
                } else {
                    FavEntry enterNewFavorite = new FavEntry(mMovieID, mMovieName, mMovieGenre, mMovieRating);
                    Log.d("T9", "adding: " + mMovieName);
                    mDb.favDao().insertFav(enterNewFavorite);
                    favorite = getString(R.string.Yes);
                }
            }
        });

    }

    public void onFabClicked(View v) {


        int test = MainActivity.getNumFavs();
        List<Movie> favMovies = MainActivity.getFavMovies();

        if (MainActivity.getNumFavs() < 10) {
            Toast.makeText(this, "Please select at least 10 favs first!", Toast.LENGTH_LONG).show();
        } else {
            buttonPressed = 1;
            int checkCode = 0;
            String movieIDQuery = "";
            String resultsString = "";


            ArrayList<String> result = movieMeProcessor.process();
            Random rand = new Random();

            while (checkCode == 0) {
                movieIDQuery = getString(R.string.API_Search_Part1) + getString(R.string.API_key) + getString(R.string.API_Search_Part2)
                        + (rand.nextInt(10) + 1) + getString(R.string.API_Search_Part3) + result.get(1) + getString(R.string.API_Search_Part4) + result.get(0)
                        + getString(R.string.API_Search_Part5);

                resultsString = "";

                try {
                    URL testURL = new URL(movieIDQuery);
                    resultsString = new apiCall().execute(testURL).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Log.d("t7", resultsString);

                if (resultsString.length() > 200) {
                    checkCode = 1;
                }
            }

            int favCheck = 0;
            Movie movieMe;

            ArrayList<Movie> movieMeResults;
            movieMeResults = JsonUtils.parseApiResult(resultsString);

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
                    Log.d("FAB2", movieMe.getMovieName());
                } else {
                    Log.d("FAB2", "Recommended a favorite starting over");
                }
                reloadActivity(movieMe);
            }
        }
    }

    public void populateWithNewMovie(Movie movieMe) {
        mMovieName = movieMe.getMovieName();
        mMovieGenre = movieMe.getGenre();
        mMovieRating = movieMe.getUserRating();

        String movieRatingOutOfTen = mMovieRating + "/10";

        mDate_Rating.setText(movieRatingOutOfTen);
        mSynopsis.setText(movieMe.getSynopsis());

        String backdropImgURL = getString(R.string.API_IMG_URL_BASE_342) + movieMe.getBackdropURL();
        Picasso.with(this).load(backdropImgURL).into(mToolbarPoster);

        setRunTimeTrailerReviews(movieMe);

        mTrailerBottomBar.setVisibility(View.GONE);
        setTrailersVisibilityAndContent();
        setReviewVisibilityAndContent();

        if (!movieTrailerURLS.isEmpty()) {
            playerFragment.getView().setVisibility(View.VISIBLE);
            mPlayer.cueVideo(movieTrailerURLS.get(0));
        } else {
            playerFragment.getView().setVisibility(View.GONE);
        }

        mMovieID = movieMe.getId();
        Log.d("T9", "populating with new movie ID: " + mMovieID);
        favorite = (getString(R.string.No));
        mCollapseLayout.setTitle(movieMe.getMovieName());

        if (!movieTrailerURLS.isEmpty()) {
            playerFragment.initialize(getString(R.string.Youtube_API_Key), this);
        }

    }

    public void reloadActivity(Movie movieMe) {
        Context context = this;
        Class destination = movieActivity.class;

        final Intent goToMovieActivity = new Intent(context, destination);
        goToMovieActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        goToMovieActivity.putExtra(getString(R.string.Movie_Name), movieMe.getMovieName());
        goToMovieActivity.putExtra(getString(R.string.Movie_Img_Url), movieMe.getImageURL());
        goToMovieActivity.putExtra(getString(R.string.Movie_Synopsis), movieMe.getSynopsis());
        goToMovieActivity.putExtra(getString(R.string.Movie_Rating), movieMe.getUserRating());
        goToMovieActivity.putExtra(getString(R.string.Movie_Release_Date), movieMe.getReleaseDate());
        goToMovieActivity.putExtra(getString(R.string.Movie_ID_URL), movieMe.getMovieIdURL());
        goToMovieActivity.putExtra(getString(R.string.Movie_ID), movieMe.getId());
        goToMovieActivity.putExtra(getString(R.string.Movie_Backdrop), movieMe.getBackdropURL());
        goToMovieActivity.putExtra(getString(R.string.Movie_Genre), movieMe.getGenre());
        goToMovieActivity.putExtra(getString(R.string.Is_Fav_Key), getString(R.string.No));

        startActivity(goToMovieActivity);
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
