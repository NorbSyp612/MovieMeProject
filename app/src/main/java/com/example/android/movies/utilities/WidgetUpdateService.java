package com.example.android.movies.utilities;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.R;
import com.example.android.movies.database.AppDatabase;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class WidgetUpdateService extends JobIntentService {

    private static Movie movieMe;
    private static AppDatabase mdb;
    public static final String ACTION_UPDATE_WIDGET = "com.example.android.movies.update_widget";
    private static Context mContext;
    public static final int JOB_ID = 1;

    public WidgetUpdateService() {

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        final String action = intent.getAction();
        if (action.equals(ACTION_UPDATE_WIDGET)) {
            initiate();
        }

    }

    public static void startActionUpdateWidget(Context context) {
        mContext = context;
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
      //  context.startService(intent);
        WidgetUpdateService.enqueueWork(context, intent);
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WidgetUpdateService.class, JOB_ID, work);
    }

    //@Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action.equals(ACTION_UPDATE_WIDGET)) {
                initiate();
            }
        }
    }

    private static void finishUpdate() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, MovieMeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget);
        MovieMeWidgetProvider.updateWidgets(mContext, appWidgetManager, movieMe.getMovieIdURL(), movieMe.getMovieName(), appWidgetIds);
    }


    public static void execute(String apiResults) {
        int favCheck = 0;
        Random rand = new Random();

        ArrayList<Movie> movieMeResults;
        movieMeResults = JsonUtils.parseApiResult(apiResults);

        if (movieMeResults == null || movieMeResults.size() < 0) {
            Timber.d("Uh oh");
        } else {
            movieMe = movieMeResults.get(rand.nextInt(movieMeResults.size() + 1));
            Timber.d(movieMe.getMovieName());
            WidgetUpdateService.finishUpdate();
        }
    }


    public void initiate() {

        String movieIDQuery = "";

        ArrayList<String> result = movieMeProcessor.process();
        Random rand = new Random();


        movieIDQuery = getString(R.string.API_Search_Part1) + getString(R.string.API_key) + getString(R.string.API_Search_Part2)
                + (rand.nextInt(10) + 1) + getString(R.string.API_Search_Part3) + result.get(1) + getString(R.string.API_Search_Part4) + result.get(0)
                + getString(R.string.API_Search_Part5);

        Timber.d(movieIDQuery);

        URL testURL = null;
        try {
            testURL = new URL(movieIDQuery);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new apiForWidget().execute(testURL);

        Timber.d(movieIDQuery);
    }
}

class apiForWidget extends AsyncTask<URL, Void, String> {

    @Override
    protected String doInBackground(URL... urls) {
        Timber.d("doing in background");
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
        if (apiResults != null) {
            WidgetUpdateService.execute(apiResults);
        }
    }
}