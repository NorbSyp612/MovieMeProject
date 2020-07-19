package my.movie.me.movies.utilities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import my.movie.me.movies.AppExecutors;
import my.movie.me.movies.Items.Movie;
import my.movie.me.movies.MainActivity;
import my.movie.me.movies.MainViewModel;
import my.movie.me.movies.database.AppDatabase;
import my.movie.me.movies.database.FavEntry;
import my.movie.me.movies.movieActivity;
import my.movie.me.movies.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.ListItemClickListener, SearchAdapter.ButtonItemClickListener, OnAsyncFinished {

    private RecyclerView mRecycle;
    private static ArrayList<Movie> movies;
    private SearchAdapter mAdapter;
    private FavEntry movieEntry;
    static List<FavEntry> favorites;
    private static ArrayList<Movie> favMovies = new ArrayList<>();
    private String favorite;
    private AppDatabase mDb;
    private int clicks;
    private String instance_clicks;
    private static int pageCount;
    private SwipeRefreshLayout swipeLayout;
    private static String baseQuery;
    private static boolean done;
    private String INSTANCE_POSITION_CODE = "IPC";
    private String INSTANCE_EXTRA = "IEX";
    private String INSTANCE_SIZE = "ISIZE";
    private static int finalSize;
    private String extra;
    private int viewPosition;
    private LinearLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_layout);

        mDb = AppDatabase.getInstance(getApplicationContext());

        movies = new ArrayList<>();
        clicks = 0;
        instance_clicks = "Clicks";
        pageCount = 1;
        mRecycle = findViewById(R.id.search_results);
        done = false;
        viewPosition = 0;
        finalSize = 0;

        swipeLayout = findViewById(R.id.search_refresh);
        swipeLayout.setRefreshing(true);


        if (savedInstanceState != null && savedInstanceState.containsKey(instance_clicks)) {
            clicks = savedInstanceState.getInt(instance_clicks, 0);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_POSITION_CODE)) {
            viewPosition = savedInstanceState.getInt(INSTANCE_POSITION_CODE);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_EXTRA)) {
            extra = savedInstanceState.getString(INSTANCE_EXTRA);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_SIZE)) {
            finalSize = savedInstanceState.getInt(INSTANCE_SIZE);
        }

        Context mContext = getApplicationContext();

        layoutManager = new LinearLayoutManager(mContext);
        mRecycle.setLayoutManager(layoutManager);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
            }
        });

        handleSearch();

    }

    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getFavs().observe(this, new Observer<List<FavEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavEntry> favEntries) {
                favorites = favEntries;

                favMovies.clear();

                double numAction = 0;
                double numAdv = 0;
                double numComedy = 0;
                double numHistory = 0;
                double numHorror = 0;
                double numDrama = 0;
                double numFantasy = 0;
                double numMystery = 0;
                double numRomance = 0;
                double numSciFi = 0;
                double numThriller = 0;
                double numWestern = 0;

                String probAction;
                String probAdv;
                String probComedy;
                String probHistory;
                String probHorror;
                String probDrama;
                String probFantasy;
                String probMystery;
                String probRomance;
                String probScifi;
                String probThriller;
                String probWestern;

                double ratingsTotal = 0;

                ArrayList<String> genres = new ArrayList<>();
                ArrayList<String> ratings = new ArrayList<>();

                for (FavEntry a : favorites) {
                    Movie addMovie = new Movie();
                    addMovie.setMovieName(a.getName());
                    favMovies.add(addMovie);
                    genres.add(a.getCategory());
                    ratings.add(a.getRating());
                    ratingsTotal = ratingsTotal + Double.parseDouble(a.getRating());
                }

                for (String b : genres) {
                    if (b.equals(getString(R.string.Action))) {
                        numAction++;
                    } else if (b.equals(getString(R.string.Adventure))) {
                        numAdv++;
                    } else if (b.equals(getString(R.string.Comedy))) {
                        numComedy++;
                    } else if (b.equals(getString(R.string.History))) {
                        numHistory++;
                    } else if (b.equals(getString(R.string.Horror))) {
                        numHorror++;
                    } else if (b.equals(getString(R.string.Drama))) {
                        numDrama++;
                    } else if (b.equals(getString(R.string.Fantasy))) {
                        numFantasy++;
                    } else if (b.equals(getString(R.string.Mystery))) {
                        numMystery++;
                    } else if (b.equals(getString(R.string.Romance))) {
                        numRomance++;
                    } else if (b.equals(getString(R.string.SciFi))) {
                        numSciFi++;
                    } else if (b.equals(getString(R.string.Thriller))) {
                        numThriller++;
                    } else if (b.equals(getString(R.string.Western))) {
                        numWestern++;
                    }
                }

                int genreSize = genres.size() + 1;
                ratingsTotal = ratingsTotal / genreSize;
                String returnRatings = "" + ratingsTotal;

                probAction = "" + (numAction / genreSize);
                probAdv = "" + (numAdv / genreSize);
                probComedy = "" + (numComedy / genreSize);
                probHistory = "" + (numHistory / genreSize);
                probHorror = "" + (numHorror / genreSize);
                probDrama = "" + (numDrama / genreSize);
                probFantasy = "" + (numFantasy / genreSize);
                probMystery = "" + (numMystery / genreSize);
                probRomance = "" + (numRomance / genreSize);
                probScifi = "" + (numSciFi / genreSize);
                probThriller = "" + (numThriller / genreSize);
                probWestern = "" + (numWestern / genreSize);


                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(getString(R.string.Return_Ratings), returnRatings);
                editor.putString(getString(R.string.Action), probAction);
                editor.putString(getString(R.string.Adventure), probAdv);
                editor.putString(getString(R.string.Comedy), probComedy);
                editor.putString(getString(R.string.History), probHistory);
                editor.putString(getString(R.string.Horror), probHorror);
                editor.putString(getString(R.string.Drama), probDrama);
                editor.putString(getString(R.string.Fantasy), probFantasy);
                editor.putString(getString(R.string.Mystery), probMystery);
                editor.putString(getString(R.string.Romance), probRomance);
                editor.putString(getString(R.string.SciFi), probScifi);
                editor.putString(getString(R.string.Thriller), probThriller);
                editor.putString(getString(R.string.Western), probWestern);


                editor.apply();

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onFabClicked(View v) {
        //   ArrayList<Movie> mainMovies = MainActivity.getMovies();
        //   favMovies = MainActivity.getFavs();
        Context bContext = getApplicationContext();

        if (favorites.isEmpty()) {
            Toast.makeText(bContext, bContext.getString(R.string.AddFavPls), Toast.LENGTH_SHORT).show();
        } else if (favorites.size() < 10) {
            Toast.makeText(bContext, bContext.getString(R.string.AddMoreThanTen), Toast.LENGTH_SHORT).show();
        } else {
            String movieIDQuery;
            movieMeProcessor processor = new movieMeProcessor();
            ArrayList<String> result = processor.process(bContext);
            Random rand = new Random();


            movieIDQuery = bContext.getString(R.string.API_Search_Part1) + bContext.getString(R.string.API_key) + bContext.getString(R.string.API_Search_Part2)
                    + (rand.nextInt(10) + 1) + bContext.getString(R.string.API_Search_Part3) + result.get(1) + bContext.getString(R.string.API_Search_Part4) + result.get(0)
                    + bContext.getString(R.string.API_Search_Part5);


            URL testURL = null;
            try {
                testURL = new URL(movieIDQuery);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                String results = new apiCallButton().execute(testURL).get();
                executeFavButton(results);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void executeFavButton(String apiResults) {
        int favCheck = 0;
        Movie movieMe = new Movie();
        Random rand = new Random();


        ArrayList<Movie> movieMeResults;
        movieMeResults = JsonUtils.parseApiResult(apiResults);

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
            goToMovieMeDetail(movieMe);
        }

    }

    public void goToMovieMeDetail(Movie movieMe) {
        clicks++;

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
        goToMovieActivity.putExtra(mContext.getString(R.string.Movie_Genre), movieMe.getGenresString());
        goToMovieActivity.putExtra(mContext.getString(R.string.Is_Fav_Key), movieMe.getFav());

        mContext.startActivity(goToMovieActivity);
    }

    public void populateUI() {
        Log.d("TEST", "Populating UI");
        int numMovies = movies.size();
        mAdapter = new SearchAdapter(numMovies, this, this, movies, favMovies);
        mRecycle.setAdapter(mAdapter);
        mRecycle.setHasFixedSize(false);
        mRecycle.scrollToPosition(viewPosition);

        setupViewModel();

        mRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && !swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(true);
                    String page = Integer.toString(pageCount);
                    URL tester = NetworkUtils.jsonRequest(baseQuery, page);
                    new extra(SearchActivity.this, tester).execute();
                }
            }
        });


        swipeLayout.setRefreshing(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleSearch();
        super.onNewIntent(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(instance_clicks, clicks);
        viewPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        outState.putInt(INSTANCE_POSITION_CODE, viewPosition);
        outState.putString(INSTANCE_EXTRA, extra);
        outState.putInt(INSTANCE_SIZE, movies.size());
        Log.d("TEST", "OUTSTATE size: " + movies.size());
        super.onSaveInstanceState(outState);
    }

    private void handleSearch() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            setTitle(searchQuery);
            baseQuery = getString(R.string.API_Search_Query_Base) + searchQuery + getString(R.string.API_Search_Query_End);
            String one = Integer.toString(pageCount);
            URL testURL = NetworkUtils.jsonRequest(baseQuery, one);
            if (extra != null && extra.equals("Yes")) {
                Log.d("TEST", "Doing Extra");
                new searchExtra(this, this, testURL).execute();
            } else {
                Log.d("TEST", "Doing search");
                new search(this, testURL).execute();
            }
        }
    }

    @Override
    public void onListItemClick(View itemView, int clickedItemIndex) {
        if (!movies.isEmpty()) {
            clicks++;
            Context context = SearchActivity.this;
            Class destination = movieActivity.class;

            final Intent goToMovieActivity = new Intent(context, destination);

            goToMovieActivity.putExtra(getString(R.string.Movie_Name), movies.get(clickedItemIndex).getMovieName());
            goToMovieActivity.putExtra(getString(R.string.Movie_Img_Url), movies.get(clickedItemIndex).getImageURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_Synopsis), movies.get(clickedItemIndex).getSynopsis());
            goToMovieActivity.putExtra(getString(R.string.Movie_Rating), movies.get(clickedItemIndex).getUserRating());
            goToMovieActivity.putExtra(getString(R.string.Movie_Release_Date), movies.get(clickedItemIndex).getReleaseDate());
            goToMovieActivity.putExtra(getString(R.string.Movie_ID_URL), movies.get(clickedItemIndex).getMovieIdURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_ID), movies.get(clickedItemIndex).getId());
            goToMovieActivity.putExtra(getString(R.string.Movie_Backdrop), movies.get(clickedItemIndex).getBackdropURL());
            goToMovieActivity.putExtra(getString(R.string.Movie_Genre), movies.get(clickedItemIndex).getGenresString());

            String movieID = movies.get(clickedItemIndex).getId();
            String isFavorite = getString(R.string.No);

            for (FavEntry a : favorites) {
                if (a.getId().equals(movieID)) {
                    isFavorite = getString(R.string.Yes);
                }
            }


            goToMovieActivity.putExtra(getString(R.string.Is_Fav_Key), isFavorite);

            startActivity(goToMovieActivity);
        }
    }

    @Override
    public void onButtonClick(View itemView, final int clickedItemIndex) {

        clicks++;
        favorite = getString(R.string.No);


        for (FavEntry a : favorites) {
            if (a.getId().equals(movies.get(clickedItemIndex).getId())) {
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
                    FavEntry enterNewFavorite = new FavEntry(movies.get(clickedItemIndex).getId(), movies.get(clickedItemIndex).getMovieName(), movies.get(clickedItemIndex).getGenre(), movies.get(clickedItemIndex).getUserRating(), movies.get(clickedItemIndex).getGenre());
                    mDb.favDao().insertFav(enterNewFavorite);
                    favorite = getString(R.string.Yes);
                }
            }
        });


    }

    @Override
    public void onAsyncFinished(ArrayList<Movie> o, String key) {

        if (done) {
            swipeLayout.setRefreshing(false);
        } else {
            if (!o.isEmpty()) {
                if (key.equals("Key")) {
                    Log.d("TEST", "Doing extra in finish");
                    pageCount++;
                    movies.addAll(o);
                    mAdapter.notifyItemInserted(movies.size() - 1);
                    mAdapter.notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);
                    extra = "Yes";
                } else if (key.equals("searchExtra")) {
                    extra = "Yes";
                    populateUI();
                } else {
                    if (pageCount < 5) {
                        pageCount++;
                        movies.addAll(o);
                        String page = Integer.toString(pageCount);
                        URL tester = NetworkUtils.jsonRequest(baseQuery, page);
                        new search(this, tester).execute();
                    } else {
                        populateUI();
                        extra = "";
                    }
                }
            } else {
                populateUI();
                extra = "";
            }
        }
    }

    static class search extends AsyncTask<URL, Void, ArrayList<Movie>> {

        private OnAsyncFinished onAsyncFinished;
        private ArrayList<Movie> moviesResult;
        private URL testURL;

        public search(OnAsyncFinished onAsyncFinished, URL url) {
            this.onAsyncFinished = onAsyncFinished;
            this.testURL = url;

        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            try {
                String apiResult = NetworkUtils.getResponseFromHttpUrl(this.testURL);
                moviesResult = JsonUtils.parseSearchResult(apiResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            onAsyncFinished.onAsyncFinished(movies, "no");
        }
    }

    static class searchExtra extends AsyncTask<URL, Void, ArrayList<Movie>> {

        private WeakReference<SearchActivity> reference;
        private OnAsyncFinished onAsyncFinished;
        private ArrayList<Movie> moviesResult;
        private URL testURL;

        public searchExtra(SearchActivity Context, OnAsyncFinished onAsyncFinished, URL url) {
            reference = new WeakReference<>(Context);
            this.onAsyncFinished = onAsyncFinished;
            this.testURL = url;

        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            try {
                String apiResult = NetworkUtils.getResponseFromHttpUrl(this.testURL);
                moviesResult = JsonUtils.parseSearchResult(apiResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> o) {
            SearchActivity activity = reference.get();

            Log.d("TEST", "Post Execute");
            Log.d("TEST", "movies array size: " + movies.size());
            Log.d("TEST", "final size: " + finalSize);

            if (movies.size() < finalSize) {
                pageCount++;
                movies.addAll(o);
                String page = Integer.toString(pageCount);
                URL tester = NetworkUtils.jsonRequest(baseQuery, page);
                new searchExtra(activity, activity, tester).execute();
            } else {
                onAsyncFinished.onAsyncFinished(movies, "searchExtra");
            }
        }
    }

    static class extra extends AsyncTask<URL, Void, ArrayList<Movie>> {
        private OnAsyncFinished onAsyncFinished;
        private ArrayList<Movie> moviesResult = new ArrayList<>();
        private URL testURL;

        public extra(OnAsyncFinished onAsyncFinished, URL url) {
            this.onAsyncFinished = onAsyncFinished;
            this.testURL = url;
        }

        @Override
        protected ArrayList<Movie> doInBackground(URL... urls) {
            try {
                String apiResult = NetworkUtils.getResponseFromHttpUrl(this.testURL);
                moviesResult = JsonUtils.parseSearchResult(apiResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> o) {
            if (o.isEmpty()) {
                SearchActivity.done = true;
            }
            onAsyncFinished.onAsyncFinished(o, "Key");
        }
    }

    static class apiCallButton extends AsyncTask<URL, Void, String> {

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

