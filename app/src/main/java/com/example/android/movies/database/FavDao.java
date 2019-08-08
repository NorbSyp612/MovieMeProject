package com.example.android.movies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
