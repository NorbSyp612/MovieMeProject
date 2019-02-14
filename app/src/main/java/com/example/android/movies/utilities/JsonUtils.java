package com.example.android.movies.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.MainActivity;
import com.example.android.movies.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class JsonUtils {

    public static ArrayList<String> getVideoLinks(String apiResult) {

        ArrayList<String> trailerLinks = new ArrayList<String>();

        try {

            if (apiResult == null) {
                return trailerLinks;
            }

            JSONObject jResult = new JSONObject(apiResult);

            JSONArray jResults = jResult.getJSONArray("results");

            for (int i = 0; i < jResults.length(); i++) {
                JSONObject trailerResult = jResults.getJSONObject(i);

                String trailerType = trailerResult.getString("type");

                if (trailerType.equals("Trailer")) {

                    String trailerKey = trailerResult.getString("key");

                    String trailerURL = trailerKey;
                    trailerLinks.add(trailerURL);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailerLinks;
    }

    public static String movieIDtest(String apiResult) {

        String movieRunTime = "";

        try {

            if (apiResult == null) {
                return movieRunTime;
            }

            JSONObject jResult = new JSONObject(apiResult);

            movieRunTime = jResult.getString("runtime");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (movieRunTime.equals("nullmin")) {
            movieRunTime = "Runtime N/A";
        }

        return movieRunTime;
    }

    public static ArrayList<Movie> parseApiResult(String apiResult) {

        ArrayList<Movie> parsedResults = new ArrayList<Movie>();

        try {

            if (apiResult == null) {
                return parsedResults;
            }

            JSONObject jResult = new JSONObject(apiResult);

            JSONArray jMovies = jResult.getJSONArray("results");

            for (int i = 0; i < jMovies.length(); i++) {
                Movie addMovie = new Movie();
                JSONObject movie = jMovies.getJSONObject(i);

                String movieName = movie.getString("title");
                String movieImageURL = movie.getString("poster_path");
                String movieSynopsis = movie.getString("overview");
                String movieUserRating = movie.getString("vote_average");
                String movieReleaseDate = movie.getString("release_date");


                String movieId = movie.getString("id");
                //  String movieURLstart = "https://api.themoviedb.org/3/movie/";
                // String movieURLend = "?api_key=cf302f54886739895a2c28626d65e40d&language=en-US";
                //  String movieURLstring = movieURLstart + movieId + movieURLend;


                //    URL movieURL = new URL(movieURLstring);

                //    String movieResults = new MainActivity.apiCall().execute(movieURL).get();

                addMovie.setMovieName(movieName);
                addMovie.setImageURL(movieImageURL);
                addMovie.setSynopsis(movieSynopsis);
                addMovie.setUserRating(movieUserRating);
                addMovie.setReleaseDate(movieReleaseDate);
                addMovie.setId(movieId);

                parsedResults.add(addMovie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //    } catch (MalformedURLException e) {
            //        e.printStackTrace();
            //    } catch (ExecutionException e) {
            //        e.printStackTrace();
            //    } catch (InterruptedException e) {
            //        e.printStackTrace();
        }

        return parsedResults;
    }
}
