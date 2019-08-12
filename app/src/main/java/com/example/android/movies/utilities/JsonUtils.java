package com.example.android.movies.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.MainActivity;
import com.example.android.movies.R;
import com.example.android.movies.database.AppDatabase;
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

            if (jResults == null) {
                return trailerLinks;
            }

            for (int i = 0; i < jResults.length(); i++) {
                JSONObject trailerResult = jResults.getJSONObject(i);

                String trailerType = trailerResult.getString("type");
                String site = trailerResult.getString("site");

                if (trailerType.equals("Trailer") && site.equals("YouTube")) {

                    String trailerKey = trailerResult.getString("key");
                    trailerLinks.add(trailerKey);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailerLinks;
    }

    public static ArrayList<String> getReviews(String apiResult) {

        ArrayList<String> movieReviews = new ArrayList<String>();

        try {

            if (apiResult == null) {
                return movieReviews;
            }

            JSONObject jResult = new JSONObject(apiResult);

            JSONArray jResults = jResult.getJSONArray("results");

            for (int i = 0; i < jResults.length(); i++) {
                JSONObject reviewsResult = jResults.getJSONObject(i);
                String reviewContent = reviewsResult.getString("content");
                movieReviews.add(reviewContent);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movieReviews;
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

    public static Movie parseFavoriteMovie(String apiResult) {
        Movie movie = new Movie();

        try {

            if (apiResult == null) {
                movie = null;
                return movie;
            }


            JSONObject jResult = new JSONObject(apiResult);


            String movieName = jResult.getString("title");
            String movieImageURL = jResult.getString("poster_path");
            String movieSynopsis = jResult.getString("overview");
            String movieUserRating = jResult.getString("vote_average");
            String movieReleaseDate = jResult.getString("release_date");
            String movieId = jResult.getString("id");

            movie.setMovieName(movieName);
            movie.setImageURL(movieImageURL);
            movie.setSynopsis(movieSynopsis);
            movie.setUserRating(movieUserRating);
            movie.setReleaseDate(movieReleaseDate);
            movie.setId(movieId);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
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
                String movieBackdropURL = movie.getString("backdrop_path");
                String movieSynopsis = movie.getString("overview");
                String movieUserRating = movie.getString("vote_average");
                String movieReleaseDate = movie.getString("release_date");
                String movieId = movie.getString("id");

                JSONArray genres = movie.getJSONArray("genre_ids");

                for (int b = 0; b < genres.length(); b++) {
                    Log.d("JSON", genres.getString(b));
                }


                addMovie.setMovieName(movieName);
                addMovie.setImageURL(movieImageURL);
                addMovie.setSynopsis(movieSynopsis);
                addMovie.setUserRating(movieUserRating);
                addMovie.setReleaseDate(movieReleaseDate);
                addMovie.setId(movieId);
                addMovie.setBackdropURL(movieBackdropURL);

                parsedResults.add(addMovie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parsedResults;
    }
}
