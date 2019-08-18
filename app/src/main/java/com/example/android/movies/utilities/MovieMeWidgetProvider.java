package com.example.android.movies.utilities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.movies.MainActivity;
import com.example.android.movies.R;
public class MovieMeWidgetProvider extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String movieURL, String movieName, int appWidgetId) {

        RemoteViews views;

        views = new RemoteViews(context.getPackageName(), R.layout.movieme_widget);
        views.setTextViewText(R.id.widget_movieName, movieName);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_movieName, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        WidgetUpdateService.startActionUpdateWidget(context);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, String movieURL, String movieName, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, movieURL, movieName, appWidgetId);
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
