package com.example.android.movies.Items;

public class Movie {

    private String movieName;
    private String imageURL;
    private String synopsis;
    private String userRating;
    private String releaseDate;
    private String id;
    private String runtime;
    private String movieIdURL;

    public Movie() {
    }

    public void setId(String movieId) {
        this.id = movieId;
    }

    public String getId() {
        return this.id;
    }

    public void setRunTime(String movieRunTime) {
        this.runtime = movieRunTime;
    }

    public String getRuntime() {
        return this.runtime;
    }

    public Movie(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieIdURL(){
        String movieURLstart = "https://api.themoviedb.org/3/movie/";
        String movieURLend = "?api_key=cf302f54886739895a2c28626d65e40d&language=en-US";
        String movieURLstring = movieURLstart + this.id + movieURLend;

        return movieURLstring;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieName() {
        return this.movieName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getUserRating() {
        return this.userRating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }
}
