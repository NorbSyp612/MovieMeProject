package my.movie.me.movies;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import my.movie.me.movies.database.AppDatabase;
import my.movie.me.movies.database.FavEntry;

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
