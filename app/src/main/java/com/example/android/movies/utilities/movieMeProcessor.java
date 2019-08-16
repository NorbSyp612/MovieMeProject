package com.example.android.movies.utilities;

import android.content.Context;
import android.util.Log;

import com.example.android.movies.R;
import com.example.android.movies.database.FavEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Collections;

public class movieMeProcessor {

    private static List<FavEntry> favorites;
    private static Random rand;
    private static Context context;

    public movieMeProcessor(List<FavEntry> favs, Context appContext) {
        favorites = favs;
        rand = new Random();
        context = appContext;
    }

    public static ArrayList<String> process() {

        ArrayList<String> genres = new ArrayList<>();
        ArrayList<String> ratings = new ArrayList<>();
        ArrayList<String> finalGenreAndRating = new ArrayList<>();

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

        double ratingsTotal = 0;

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
            if (b.equals(context.getString(R.string.Action))) {
                numAction++;
            } else if (b.equals(context.getString(R.string.Adventure))) {
                numAdv++;
            } else if (b.equals(context.getString(R.string.Comedy))) {
                numComedy++;
            } else if (b.equals(context.getString(R.string.History))) {
                numHistory++;
            } else if (b.equals(context.getString(R.string.Horror))) {
                numHorror++;
            } else if (b.equals(context.getString(R.string.Drama))) {
                numDrama++;
            } else if (b.equals(context.getString(R.string.Fantasy))) {
                numFantasy++;
            } else if (b.equals(context.getString(R.string.Mystery))) {
                numMystery++;
            } else if (b.equals(context.getString(R.string.Romance))) {
                numRomance++;
            } else if (b.equals(context.getString(R.string.SciFi))) {
                numSciFi++;
            } else if (b.equals(context.getString(R.string.Thriller))) {
                numThriller++;
            } else if (b.equals(context.getString(R.string.Western))) {
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

        map.put(context.getString(R.string.Action), probAction);
        map.put(context.getString(R.string.Adventure), probAdv);
        map.put(context.getString(R.string.Comedy), probComedy);
        map.put(context.getString(R.string.History), probHistory);
        map.put(context.getString(R.string.Horror), probHorror);
        map.put(context.getString(R.string.Drama), probDrama);
        map.put(context.getString(R.string.Fantasy), probFantasy);
        map.put(context.getString(R.string.Mystery), probMystery);
        map.put(context.getString(R.string.Romance), probRomance);
        map.put(context.getString(R.string.SciFi), probScifi);
        map.put(context.getString(R.string.Thriller), probThriller);
        map.put(context.getString(R.string.Western), probWestern);


        while (categoryCheck == 0) {
            double randomDouble = rand.nextDouble();

            Collections.shuffle(genres);

            int randomInt = rand.nextInt(genres.size());

            if (map.get(genres.get(randomInt)) != null) {
                if (map.get(genres.get(randomInt)) > randomDouble) {
                    finalCategory = genres.get(randomInt);
                    categoryCheck = 1;
                }
            }
        }

        for (String c : ratings) {
            ratingsTotal = ratingsTotal + Double.parseDouble(c);
        }

        ratingsTotal = ratingsTotal / ratings.size();
        ratingsTotal = ratingsTotal - (rand.nextDouble() + .25);

        if (ratingsTotal > 8.3) {
            ratingsTotal = ratingsTotal - 0.5;
        }


        String finaRating = "" + ratingsTotal;

        finalCategory = getGenreId(finalCategory);

        finalGenreAndRating.add(finalCategory);
        finalGenreAndRating.add(finaRating);

        return finalGenreAndRating;
    }

    public static String getGenreId(String genre) {
        if (genre.equals(context.getString(R.string.Action))) {
            return context.getString(R.string.Action_ID);
        } else if (genre.equals(context.getString(R.string.Adventure))) {
            return context.getString(R.string.Adv_ID);
        } else if (genre.equals(context.getString(R.string.Comedy))) {
            return context.getString(R.string.Comedy_ID);
        } else if (genre.equals(context.getString(R.string.Drama))) {
            return context.getString(R.string.Drama_ID);
        } else if (genre.equals(context.getString(R.string.Fantasy))) {
            return context.getString(R.string.Fantasy_ID);
        } else if (genre.equals(context.getString(R.string.History))) {
            return context.getString(R.string.History_ID);
        } else if (genre.equals(context.getString(R.string.Horror))) {
            return context.getString(R.string.Horror_ID);
        } else if (genre.equals(context.getString(R.string.Mystery))) {
            return context.getString(R.string.Mystery_ID);
        } else if (genre.equals(context.getString(R.string.Romance))) {
            return context.getString(R.string.Romance_ID);
        } else if (genre.equals(context.getString(R.string.SciFi))) {
            return context.getString(R.string.SciFi_ID);
        } else if (genre.equals(context.getString(R.string.Thriller))) {
            return context.getString(R.string.Thriller_ID);
        } else if (genre.equals(context.getString(R.string.Western))) {
            return context.getString(R.string.Western_ID);
        }
        return "";
    }
}
