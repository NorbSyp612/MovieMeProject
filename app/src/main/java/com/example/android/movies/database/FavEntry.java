package com.example.android.movies.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favs")
public class FavEntry {

    @PrimaryKey(autoGenerate = true)
    private int key;
    private String id;
    private String name;
    private String category;
    private String rating;
    private String genre;

    @Ignore
    public FavEntry(String id, String name, String category, String rating, String genre) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.genre = genre;
    }


    public FavEntry(int key, String id, String name, String category, String rating, String genre) {
        this.key = key;
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.genre = genre;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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
