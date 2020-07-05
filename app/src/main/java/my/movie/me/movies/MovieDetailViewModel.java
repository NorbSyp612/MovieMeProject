package my.movie.me.movies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import my.movie.me.movies.database.AppDatabase;
import my.movie.me.movies.database.FavEntry;

public class MovieDetailViewModel extends ViewModel {

    private LiveData<FavEntry> fav;

    public MovieDetailViewModel (AppDatabase database, String movieID) {
        fav = database.favDao().loadFavById(movieID);
    }

}
