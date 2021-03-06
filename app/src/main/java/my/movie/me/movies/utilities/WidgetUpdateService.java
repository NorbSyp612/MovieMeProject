package my.movie.me.movies.utilities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import my.movie.me.movies.R;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import my.movie.me.movies.Items.Movie;

import my.movie.me.movies.database.AppDatabase;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

public class WidgetUpdateService extends JobIntentService {

    private static Movie movieMe;
    public static final String ACTION_UPDATE_WIDGET = "com.example.android.movies.update_widget";
    public static final int JOB_ID = 1;
    private Intent mIntent;

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
        Timber.uprootAll();
        Timber.plant(new Timber.DebugTree());
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(ACTION_UPDATE_WIDGET);
        WidgetUpdateService.enqueueWork(context, intent);
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WidgetUpdateService.class, JOB_ID, work);

    }


    private static void finishUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MovieMeWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget);
        MovieMeWidgetProvider.updateWidgets(context, appWidgetManager, movieMe.getImageURL(), movieMe.getMovieName(), movieMe.getId(), appWidgetIds);
    }


    public void execute(Context context, String apiResults) {
        Random rand = new Random();

        ArrayList<Movie> movieMeResults;
        movieMeResults = JsonUtils.parseApiResult(apiResults);


        if (movieMeResults == null) {
            initiate();
        } else {
            movieMe = movieMeResults.get(rand.nextInt(movieMeResults.size()));
            WidgetUpdateService.finishUpdate(context);
        }
    }


    public void initiate() {

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());

        movieMeProcessor movieMeProcessor = new movieMeProcessor();

        String movieIDQuery = "";

        ArrayList<String> result = movieMeProcessor.process(getApplicationContext());
        Random rand = new Random();


        movieIDQuery = getString(R.string.API_Search_Part1) + getString(R.string.API_key) + getString(R.string.API_Search_Part2)
                + (rand.nextInt(10) + 1) + getString(R.string.API_Search_Part3) + result.get(1) + getString(R.string.API_Search_Part4) + result.get(0)
                + getString(R.string.API_Search_Part5);


        URL testURL = null;
        try {
            testURL = new URL(movieIDQuery);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new apiForWidget(this).execute(testURL);

    }
}

class apiForWidget extends AsyncTask<URL, Void, String> {

    private WeakReference<WidgetUpdateService> mainReference;
    WidgetUpdateService service;

    apiForWidget(WidgetUpdateService context) {
        mainReference = new WeakReference<>(context);
        service = mainReference.get();


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

        service = mainReference.get();


        return apiResult;
    }

    @Override
    protected void onPostExecute(String apiResults) {

        service = mainReference.get();
        if (service == null) return;


        if (apiResults != null) {
            service.execute(service.getApplicationContext(), apiResults);
        }
    }
}