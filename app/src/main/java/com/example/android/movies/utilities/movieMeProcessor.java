package com.example.android.movies.utilities;

import android.content.Context;
import android.util.Log;

import com.example.android.movies.database.FavEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Collections;

public class movieMeProcessor {

    private List<FavEntry> favorites;
    private Random rand;

    public movieMeProcessor(List<FavEntry> favs) {
        favorites = favs;
        rand = new Random();
    }

    public void process() {

        ArrayList<String> genres = new ArrayList<>();
        ArrayList<String> ratings = new ArrayList<>();

        double numAction = 0;
        double numAdv = 0;
        double numComedy = 0;
        double numHistory = 0;
        double numHorror = 0;
        double numDrama = 0;
        double numFantasy = 0;
        double numMystery = 0;
        double numRomance = 0;
        double numSciFi = 0;
        double numThriller = 0;
        double numWestern = 0;

        double probAction = 0;
        double probAdv = 0;
        double probComedy = 0;
        double probHistory = 0;
        double probHorror = 0;
        double probDrama = 0;
        double probFantasy = 0;
        double probMystery = 0;
        double probRomance = 0;
        double probScifi = 0;
        double probThriller = 0;
        double probWestern = 0;

        int categoryCheck = 0;

        String finalCategory = "";

        for (FavEntry a : favorites) {
            if (!genres.contains(a.getCategory())) {
                genres.add(a.getCategory());
            }
            ratings.add(a.getRating());
        }

        HashMap<String, Double> map = new HashMap<>();

        for (String b : genres) {
            if (b.equals("Action")) {
                numAction++;
            } else if (b.equals("Adventure")) {
                numAdv++;
            } else if (b.equals("Comedy")) {
                numComedy++;
            } else if (b.equals("History")) {
                numHistory++;
            } else if (b.equals("Horror")) {
                numHorror++;
            } else if (b.equals("Drama")) {
                numDrama++;
            } else if (b.equals("Fantasy")) {
                numFantasy++;
            } else if (b.equals("Mystery")) {
                numMystery++;
            } else if (b.equals("Romance")) {
                numRomance++;
            } else if (b.equals("SciFi")) {
                numSciFi++;
            } else if (b.equals("Thriller")) {
                numThriller++;
            } else if (b.equals("Western")) {
                numWestern++;
            }
        }

        probAction = numAction / genres.size();
        probAdv = numAdv / genres.size();
        probComedy = numComedy / genres.size();
        probHistory = numHistory / genres.size();
        probHorror = numHorror / genres.size();
        probDrama = numDrama / genres.size();
        probFantasy = numFantasy / genres.size();
        probMystery = numMystery / genres.size();
        probRomance = numRomance / genres.size();
        probScifi = numSciFi / genres.size();
        probThriller = numThriller / genres.size();
        probWestern = numWestern / genres.size();

        map.put("Action", probAction);
        map.put("Adventure", probAdv);
        map.put("Comedy", probComedy);
        map.put("History", probHistory);
        map.put("Horror", probHorror);
        map.put("Drama", probDrama);
        map.put("Fantasy", probFantasy);
        map.put("Mystery", probMystery);
        map.put("Romance", probRomance);
        map.put("SciFi", probScifi);
        map.put("Thriller", probThriller);
        map.put("Western", probWestern);


        int checkCounter = 0;

        while (categoryCheck == 0) {
            double randomDouble = rand.nextDouble();
            Log.d("FAB", "Genres size: " + genres.size());

            Collections.shuffle(genres);

            int randomInt = rand.nextInt(genres.size());

            Log.d("FAB", "random int is: " + randomInt);


            if (map.get(genres.get(randomInt)) != null) {
                Log.d("FAB", "Random double is: " + randomDouble);
                Log.d("FAB", "Random genre is: " + genres.get(randomInt));
                Log.d("FAB", "Random map result is: " + map.get(genres.get(randomInt)));

                if (map.get(genres.get(randomInt)) > randomDouble) {
                    finalCategory = genres.get(randomInt);
                    categoryCheck = 1;
                }

            }

        }

        Log.d("FAB", "final category is: " + finalCategory);
    }
}
