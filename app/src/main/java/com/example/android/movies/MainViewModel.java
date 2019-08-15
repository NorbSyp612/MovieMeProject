package com.example.android.movies;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
