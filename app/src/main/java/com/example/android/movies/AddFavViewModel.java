package com.example.android.movies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

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
