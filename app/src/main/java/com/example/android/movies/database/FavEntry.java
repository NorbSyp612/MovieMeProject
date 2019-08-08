package com.example.android.movies.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "favs")
public class FavEntry {

    @PrimaryKey(autoGenerate = true)
    private int key;
    private String id;
    private String name;

    @Ignore
    public FavEntry(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public FavEntry(int key, String id, String name) {
        this.key = key;
        this.id = id;
        this.name = name;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

   public String getName() {
        return this.name;
   }

   public void setName(String name) {
        this.name = name;
   }
}
