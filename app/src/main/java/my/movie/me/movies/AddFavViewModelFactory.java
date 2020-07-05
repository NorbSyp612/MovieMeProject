package my.movie.me.movies;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import my.movie.me.movies.database.AppDatabase;

public class AddFavViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final String mMovieID;

    public AddFavViewModelFactory(AppDatabase database, String movieID) {
        mDb = database;
        mMovieID = movieID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new AddFavViewModel(mDb, mMovieID);
    }
}
