package com.example.android.movies;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.android.movies.database.AppDatabase;

import org.jetbrains.annotations.NotNull;

public class MovieDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final String mMovieID;

    public MovieDetailViewModelFactory(AppDatabase database, String MovieID) {
        mDb = database;
        mMovieID = MovieID;
    }

    @NotNull
    @Override
    public <T extends ViewModel> T create(@NotNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieDetailViewModel(mDb, mMovieID);
    }
}

