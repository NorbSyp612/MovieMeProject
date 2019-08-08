package com.example.android.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;

public class MovieDetailViewModel extends ViewModel {

    private LiveData<FavEntry> fav;

    public MovieDetailViewModel (AppDatabase database, String movieID) {
        fav = database.favDao().loadFavById(movieID);
    }

    public LiveData<FavEntry> getFav() {
        return fav;
    }
}
