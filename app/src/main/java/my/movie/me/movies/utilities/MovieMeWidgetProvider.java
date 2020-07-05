package my.movie.me.movies.utilities;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.widget.RemoteViews;

import my.movie.me.movies.R;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import timber.log.Timber;


public class MovieMeWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String movieURL, String movieName, String movieID, int appWidgetId) {

        RemoteViews views;

        views = new RemoteViews(context.getPackageName(), R.layout.movieme_widget);
        views.setTextViewText(R.id.widget_movieName, movieName);


        String imgURL = context.getString(R.string.API_IMG_URL_BASE_342) + movieURL;
        Timber.d(imgURL);
        Timber.d(movieID);
        Timber.d(movieName);
        views.setImageViewBitmap(R.id.widget_poster, getImageBitmap(imgURL));

       // Intent intent = new Intent(context, MainActivity.class);
       // intent.putExtra(Service_ID, movieID);
       // PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
       // views.setOnClickPendingIntent(R.id.widget_poster, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static Bitmap getImageBitmap(String url) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Timber.d(e);
        }
        return bm;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        WidgetUpdateService.startActionUpdateWidget(context);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, String movieURL, String movieName, String movieID, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, movieURL, movieName, movieID, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}
