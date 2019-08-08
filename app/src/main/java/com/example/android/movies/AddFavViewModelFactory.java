package com.example.android.movies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.example.android.movies.database.AppDatabase;

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
