package com.example.android.movies;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavEntry>> favs;

    public MainViewModel (Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        favs = database.favDao().loadAllFavs();
    }

    public LiveData<List<FavEntry>> getFavs() {
        return favs;
    }
}
