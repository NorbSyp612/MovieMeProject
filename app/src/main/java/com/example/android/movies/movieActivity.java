package com.example.android.movies;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import timber.log.Timber;

import static com.example.android.movies.MainActivity.favorites;

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
    private YouTubePlayerFragment playerFragment;
    private YouTubePlayer mPlayer;
    private int buttonPressed;
    private AdView mAdView;
    private TextView mTitleBar;
    private Context mContext;
    private TextView subTitleString;

    public static final String INSTANCE_MOVIE_ID = "MovieId";
    private static final String INSTANCE_FAV = "InstanceFAV";

    String movieRunTime;
    private ArrayList<String> movieTrailerURLS;
    private ArrayList<String> movieReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState)        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        mContext = getApplicationContext();


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

        } catch (MalformedURLException | InterruptedException | ExecutionException e) {
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
        String mReleaseDate = fromMain.getStringExtra(getResources().getString(R.string.Movie_Release_Date));
        assert mReleaseDate != null;
        mReleaseDate = mReleaseDate.substring(0, 4);

        String extras = mReleaseDate + " - " + mMovieGenre;

        subTitleString.setText(extras);

        mDate_Rating.setText(movieRatingOutOfTen);
        mSynopsis.setText(fromMain.getStringExtra(movieSynopsis));

        String backdropImgURL = getString(R.string.API_IMG_URL_BASE_342) + fromMain.getStringExtra(getString(R.string.Movie_Backdrop));
        Picasso.get().load(backdropImgURL).into(mToolbarPoster);
        mToolbarPoster.setContentDescription(mMovieName);

        mTrailerBottomBar.setVisibility(View.GONE);
        setTrailersVisibilityAndContent();
        setReviewVisibilityAndContent();

        mMovieID = fromMain.getStringExtra(getString(R.string.Movie_ID));

        favorite = fromMain.getStringExtra(getString(R.string.Is_Fav_Key));

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_FAV)) {
            favorite = savedInstanceState.getString(INSTANCE_FAV);
        }
        setupViewModel();

        mTitleBar.setText(fromMain.getStringExtra(getString(R.string.Movie_Name)));

        playerFragment.initialize(getString(R.string.Youtube_API_Key), this);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer = null;
    }

    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getFavs().observe(this, new Observer<List<FavEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavEntry> favEntries) {
                favorites = favEntries;
                setFavButton();

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
                Objects.requireNonNull(playerFragment.getView()).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        mPlayer = null;
    }


    private void intiViews() {
        mFavButtonBackground = findViewById(R.id.button_background);
        mDate_Rating = findViewById(R.id.movie_rating);
        mSynopsis = findViewById(R.id.movie_summary);
        mReviewSection = findViewById(R.id.include);
        mFirstReview = findViewById(R.id.movie_first_review);
        mSecondreview = findViewById(R.id.movie_second_review);
        mReviewSeparator = findViewById(R.id.imageBar_seperator);
        mTrailerBottomBar = findViewById(R.id.imageBar3);
        mTrailerText = findViewById(R.id.textView);
        mTopImageBar = findViewById(R.id.imageBar1);
        mToolbarPoster = findViewById(R.id.movie_toolbar_poster);
        playerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.movie_Play_First_Trailer);
        mTitleBar = findViewById(R.id.title_bar);
        mAdView = findViewById(R.id.adView);
        subTitleString = findViewById(R.id.title_extras);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(INSTANCE_FAV, favorite);
        outState.putString(INSTANCE_MOVIE_ID, mMovieID);
        outState.putInt("TESTER", buttonPressed);
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

        favorite = getString(R.string.No);

        for (FavEntry a : favorites) {
            if (a.getId().equals(mMovieID)) {
                favorite = getString(R.string.Yes);
                movieEntry = a;
            }
        }

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (favorite.equals(getString(R.string.Yes))) {
                    mDb.favDao().deleteFav(movieEntry);
                    favorite = getString(R.string.No);
                } else {
                    FavEntry enterNewFavorite = new FavEntry(mMovieID, mMovieName, mMovieGenre, mMovieRating, mMovieGenre);
                    mDb.favDao().insertFav(enterNewFavorite);
                    favorite = getString(R.string.Yes);
                }
            }
        });


    }

    public void onFabClicked(View v) {
        initiateFAB(mContext);
    }

    public void initiateFAB(Context context) {

        if (MainActivity.getNumFavs() < 10)
            Toast.makeText(context, "Please select at least 10 favs first!", Toast.LENGTH_LONG).show();
        else {
            String movieIDQuery;

            movieMeProcessor processor = new movieMeProcessor();
            ArrayList<String> result = processor.process(context);
            Random rand = new Random();

            movieIDQuery = context.getString(R.string.API_Search_Part1) + context.getString(R.string.API_key) + context.getString(R.string.API_Search_Part2)
                    + (rand.nextInt(10) + 1) + context.getString(R.string.API_Search_Part3) + result.get(1) + context.getString(R.string.API_Search_Part4) + result.get(0)
                    + context.getString(R.string.API_Search_Part5);


            URL testURL = null;
            try {
                testURL = new URL(movieIDQuery);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            new apiCallButton(this).execute(testURL);
        }
    }

    public void executeFavButton(String apiResults) {
        int favCheck = 0;
        Movie movieMe = new Movie();
        Random rand = new Random();
        ArrayList<Movie> favMovies = MainActivity.getFavMovies();
        ArrayList<Movie> movieMeResults;
        movieMeResults = JsonUtils.parseApiResult(apiResults);

        if (movieMeResults == null || movieMeResults.size() == 0) {
            Toast.makeText(mContext, mContext.getString(R.string.Something_went_wrong), Toast.LENGTH_SHORT).show();
            initiateFAB(mContext);
        } else {
            while (favCheck == 0) {
                movieMe = movieMeResults.get(rand.nextInt(movieMeResults.size()));

                favCheck = 1;

                for (Movie b : favMovies) {
                    if (b.getMovieName().equals(movieMe.getMovieName())) {
                        favCheck = 0;
                        break;
                    }

                }

                if (movieMe.getBackdropURL().equals("")) {
                    favCheck = 0;
                }


            }

            if (movieMe.getMovieName() != null) {
                reloadActivity(movieMe);
            }
        }
    }


    public void reloadActivity(Movie movieMe) {
        Context context = mContext;
        Class destination = movieActivity.class;


        final Intent goToMovieActivity = new Intent(context, destination);
        goToMovieActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Name), movieMe.getMovieName());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Img_Url), movieMe.getImageURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Synopsis), movieMe.getSynopsis());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Rating), movieMe.getUserRating());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Release_Date), movieMe.getReleaseDate());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID_URL), movieMe.getMovieIdURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_ID), movieMe.getId());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Backdrop), movieMe.getBackdropURL());
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Genre), movieMe.getGenresString());
        goToMovieActivity.putExtra(mContext.getString(R.string.Is_Fav_Key), mContext.getString(R.string.No));

        mContext.startActivity(goToMovieActivity);
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

    public static class apiCallButton extends AsyncTask<URL, Void, String> {

        private WeakReference<movieActivity> mainReference;

        apiCallButton(movieActivity context) {
            mainReference = new WeakReference<>(context);
        }

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
            movieActivity activity = mainReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (apiResults.length() < 200) {
                activity.initiateFAB(activity.mContext);
            } else {
                activity.executeFavButton(apiResults);
            }
        }
    }
}
