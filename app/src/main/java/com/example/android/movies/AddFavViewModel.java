package com.example.android.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;

public class AddFavViewModel extends ViewModel {

    private static final String TAG = "T";

    private LiveData<FavEntry> fav;

    public AddFavViewModel(AppDatabase database, String movieId) {
        fav = database.favDao().loadFavById(movieId);
    }

    public LiveData<FavEntry> getFav() {
        return fav;
    }
}
