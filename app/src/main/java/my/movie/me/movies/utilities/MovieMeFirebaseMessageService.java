package my.movie.me.movies.utilities;

import android.content.Context;
import android.content.Intent;
import my.movie.me.movies.R;
import my.movie.me.movies.movieActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MovieMeFirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Context context = getApplicationContext();
        Class destination = movieActivity.class;

        final Intent goToMovieActivity = new Intent(context, destination);

        Map<String, String> movieDetails = remoteMessage.getData();

        if (movieDetails.containsKey("GoToMovie")) {
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Name), movieDetails.get(context.getString(R.string.Movie_Name)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Img_Url), movieDetails.get(context.getString(R.string.Movie_Img_Url)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Synopsis), movieDetails.get(context.getString(R.string.Movie_Synopsis)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Rating), movieDetails.get(context.getString(R.string.Movie_Rating)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Release_Date), movieDetails.get(context.getString(R.string.Movie_Release_Date)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_ID_URL), movieDetails.get(context.getString(R.string.Movie_ID_URL)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_ID), movieDetails.get(context.getString(R.string.Movie_ID)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Backdrop),movieDetails.get(context.getString(R.string.Movie_Backdrop)));
            goToMovieActivity.putExtra(context.getString(R.string.Movie_Genre), movieDetails.get(context.getString(R.string.Movie_Genre)));
            goToMovieActivity.putExtra(context.getString(R.string.Is_Fav_Key), context.getString(R.string.No));
            startActivity(goToMovieActivity);
        }
    }
}

