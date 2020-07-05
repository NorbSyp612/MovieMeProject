package my.movie.me.movies.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavDao {

    @Query("SELECT * FROM favs ORDER BY id")
    LiveData<List<FavEntry>> loadAllFavs();

    @Insert
    void insertFav(FavEntry favEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateFav(FavEntry favEntry);

    @Delete
    void deleteFav(FavEntry favEntry);

    @Query ("DELETE FROM favs")
    void deleteAll();

    @Query("SELECT * FROM favs WHERE id = :id")
    LiveData<FavEntry> loadFavById(String id);
}
