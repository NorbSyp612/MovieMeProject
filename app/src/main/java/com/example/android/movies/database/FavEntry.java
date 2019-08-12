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
    private String category;
    private String rating;

    @Ignore
    public FavEntry(String id, String name, String category, String rating) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
    }


    public FavEntry(int key, String id, String name, String category, String rating) {
        this.key = key;
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
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

    public void setCategory(String cat) {
        this.category = cat;
    }

    public String getCategory() {
        return this.category;
    }

    public void setRating(String score) {
        this.rating = score;
    }

    public String getRating() {
        return this.rating;
    }

}
