package my.movie.me.movies.utilities;

import android.util.Log;

import my.movie.me.movies.Items.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JsonUtils {


    public static ArrayList<String> getVideoLinks(String apiResult) {

        ArrayList<String> trailerLinks = new ArrayList<>();

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
            String movieBackdropURL = jResult.getString("backdrop_path");

            String movieGenre = "";
            String genreString = "";

            JSONArray genres = jResult.getJSONArray("genres");

            int genreNum;
            int testNum;

            for (int b = 0; b < genres.length(); b++) {
                JSONObject genreObject = genres.getJSONObject(b);
                genreNum = genreObject.getInt("id");

                if (genreNum == 28) {
                    movieGenre = "Action";
                } else if (genreNum == 12) {
                    movieGenre = "Adventure";
                } else if (genreNum == 35) {
                    movieGenre = "Comedy";
                } else if (genreNum == 80) {
                    movieGenre = "Crime";
                } else if (genreNum == 18) {
                    movieGenre = "Drama";
                } else if (genreNum == 10751) {
                    movieGenre = "Family";
                } else if (genreNum == 14) {
                    movieGenre = "Fantasy";
                } else if (genreNum == 36) {
                    movieGenre = "History";
                } else if (genreNum == 27) {
                    movieGenre = "Horror";
                } else if (genreNum == 9648) {
                    movieGenre = "Mystery";
                } else if (genreNum == 10749) {
                    movieGenre = "Romance";
                } else if (genreNum == 878) {
                    movieGenre = "SciFi";
                } else if (genreNum == 53) {
                    movieGenre = "Thriller";
                } else if (genreNum == 37) {
                    movieGenre = "Western";
                } else if (genreNum == 16) {
                    movieGenre = "Animation";
                } else if (genreNum == 10402) {
                    movieGenre = "Music";
                } else if (genreNum == 10770) {
                    movieGenre = "TV Movie";
                } else if (genreNum == 10752) {
                    movieGenre = "War";
                }


                if (b > 0) {
                    genreString = genreString + ", " + movieGenre;
                } else {
                    genreString = movieGenre;
                }
            }


            movie.setMovieName(movieName);
            movie.setImageURL(movieImageURL);
            movie.setSynopsis(movieSynopsis);
            movie.setUserRating(movieUserRating);
            movie.setReleaseDate(movieReleaseDate);
            movie.setId(movieId);
            movie.setBackdropURL(movieBackdropURL);
            movie.setGenre(genreString);
            movie.setGenresString(genreString);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }

    public static ArrayList<Movie> parseApiResult(String apiResult) {
        ArrayList<Movie> parsedResults = new ArrayList<>();

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
                String movieGenre = "";
                String genreString = "";

                JSONArray genres = movie.getJSONArray("genre_ids");

                int genreNum = 0;

                for (int b = 0; b < genres.length(); b++) {
                    genreNum = Integer.parseInt(genres.getString(b));

                    if (genreNum == 28) {
                        movieGenre = "Action";
                    } else if (genreNum == 12) {
                        movieGenre = "Adventure";
                    } else if (genreNum == 35) {
                        movieGenre = "Comedy";
                    } else if (genreNum == 80) {
                        movieGenre = "Crime";
                    } else if (genreNum == 18) {
                        movieGenre = "Drama";
                    } else if (genreNum == 10751) {
                        movieGenre = "Family";
                    } else if (genreNum == 14) {
                        movieGenre = "Fantasy";
                    } else if (genreNum == 36) {
                        movieGenre = "History";
                    } else if (genreNum == 27) {
                        movieGenre = "Horror";
                    } else if (genreNum == 9648) {
                        movieGenre = "Mystery";
                    } else if (genreNum == 10749) {
                        movieGenre = "Romance";
                    } else if (genreNum == 878) {
                        movieGenre = "SciFi";
                    } else if (genreNum == 53) {
                        movieGenre = "Thriller";
                    } else if (genreNum == 37) {
                        movieGenre = "Western";
                    } else if (genreNum == 16) {
                        movieGenre = "Animation";
                    } else if (genreNum == 10402) {
                        movieGenre = "Music";
                    } else if (genreNum == 10770) {
                        movieGenre = "TV Movie";
                    } else if (genreNum == 10752) {
                        movieGenre = "War";
                    }

                    if (b > 0) {
                        genreString = genreString + ", " + movieGenre;
                    } else {
                        genreString = movieGenre;
                    }
                }


                addMovie.setMovieName(movieName);
                addMovie.setImageURL(movieImageURL);
                addMovie.setSynopsis(movieSynopsis);
                addMovie.setUserRating(movieUserRating);
                addMovie.setReleaseDate(movieReleaseDate);
                addMovie.setId(movieId);
                addMovie.setBackdropURL(movieBackdropURL);
                addMovie.setGenre(movieGenre);
                addMovie.setGenresString(genreString);

                if (!movieBackdropURL.isEmpty() && !movieGenre.isEmpty() && Double.parseDouble(movieUserRating) > 2) {
                    parsedResults.add(addMovie);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parsedResults;
    }

    public static ArrayList<Movie> parseSearchResult(String apiResult) {

        ArrayList<Movie> parsedResults = new ArrayList<>();

        try {

            if (apiResult == null) {
                Log.d("TEST", "SEARCH NULL");
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
                String movieGenre = "";

                JSONArray genres = movie.getJSONArray("genre_ids");

                int genreNum = 0;
                int testNum;
                String genreString = "";

                for (int b = 0; b < genres.length(); b++) {
                    genreNum = Integer.parseInt(genres.getString(b));

                    if (genreNum == 28) {
                        movieGenre = "Action";
                    } else if (genreNum == 12) {
                        movieGenre = "Adventure";
                    } else if (genreNum == 35) {
                        movieGenre = "Comedy";
                    } else if (genreNum == 80) {
                        movieGenre = "Crime";
                    } else if (genreNum == 18) {
                        movieGenre = "Drama";
                    } else if (genreNum == 10751) {
                        movieGenre = "Family";
                    } else if (genreNum == 14) {
                        movieGenre = "Fantasy";
                    } else if (genreNum == 36) {
                        movieGenre = "History";
                    } else if (genreNum == 27) {
                        movieGenre = "Horror";
                    } else if (genreNum == 9648) {
                        movieGenre = "Mystery";
                    } else if (genreNum == 10749) {
                        movieGenre = "Romance";
                    } else if (genreNum == 878) {
                        movieGenre = "SciFi";
                    } else if (genreNum == 53) {
                        movieGenre = "Thriller";
                    } else if (genreNum == 37) {
                        movieGenre = "Western";
                    } else if (genreNum == 16) {
                        movieGenre = "Animation";
                    } else if (genreNum == 10402) {
                        movieGenre = "Music";
                    } else if (genreNum == 10770) {
                        movieGenre = "TV Movie";
                    } else if (genreNum == 10752) {
                        movieGenre = "War";
                    }


                    if (b > 0) {
                        genreString = genreString + ", " + movieGenre;
                    } else {
                        genreString = movieGenre;
                    }
                }




                addMovie.setMovieName(movieName);
                addMovie.setImageURL(movieImageURL);
                addMovie.setSynopsis(movieSynopsis);
                addMovie.setUserRating(movieUserRating);
                addMovie.setReleaseDate(movieReleaseDate);
                addMovie.setId(movieId);
                addMovie.setBackdropURL(movieBackdropURL);
                addMovie.setGenre(movieGenre);
                addMovie.setGenresString(genreString);

                double tester = Double.parseDouble(movieUserRating);


                if (!movieBackdropURL.equals("null") && tester > .5 && !movieImageURL.equals("null") && !movieReleaseDate.equals("null") && !movieGenre.isEmpty()) {
                    parsedResults.add(addMovie);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

      //  Log.d("TEST", "Movie 1 is: " + parsedResults.get(0).getMovieName());

        return parsedResults;
    }
}
