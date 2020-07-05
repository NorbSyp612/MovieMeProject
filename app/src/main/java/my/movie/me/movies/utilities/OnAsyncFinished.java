package my.movie.me.movies.utilities;

import my.movie.me.movies.Items.Movie;

import java.util.ArrayList;

public interface OnAsyncFinished{
    void onAsyncFinished (ArrayList<Movie> o, String key);
}
