package com.example.android.movies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.movies.R;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Collections;

import timber.log.Timber;

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

            genres.add(a.getCategory());

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String Action = sharedPreferences.getString(context.getString(R.string.Action), "");
        String Adv = sharedPreferences.getString(context.getString(R.string.Adventure), "");
        String Comedy = sharedPreferences.getString(context.getString(R.string.Comedy), "");
        String History = sharedPreferences.getString(context.getString(R.string.History), "");
        String Horror = sharedPreferences.getString(context.getString(R.string.Horror), "");
        String Drama = sharedPreferences.getString(context.getString(R.string.Drama), "");
        String Fantasy = sharedPreferences.getString(context.getString(R.string.Fantasy), "");
        String Mystery = sharedPreferences.getString(context.getString(R.string.Mystery), "");
        String Romance = sharedPreferences.getString(context.getString(R.string.Romance), "");
        String Scifi = sharedPreferences.getString(context.getString(R.string.SciFi), "");
        String Thriller = sharedPreferences.getString(context.getString(R.string.Thriller), "");
        String Western = sharedPreferences.getString(context.getString(R.string.Western), "");

        int genreSize = genres.size() - 1;


        Timber.d("First prob action is: %s", (numAction / genreSize));
        Timber.d("First prob adv is: %s", (numAdv / genreSize));
        Timber.d("First prob comedy is: %s", (numComedy / genreSize));
        Timber.d("First prob history is: %s", (numHistory / genreSize));
        Timber.d("First prob horror is: %s", (numHorror / genreSize));
        Timber.d("First prob drama is: %s", (numDrama / genreSize));
        Timber.d("First prob fantasy is: %s", (numFantasy / genreSize));
        Timber.d("First prob mystery is: %s", (numMystery / genreSize));
        Timber.d("First prob romance is: %s", (numRomance / genreSize));
        Timber.d("First prob scifi is: %s", (numSciFi / genreSize));
        Timber.d("First prob thriller is: %s", (numThriller / genreSize));
        Timber.d("First prob western is: %s", (numWestern / genreSize));


        probAction = Double.parseDouble(Action);
        probAdv = Double.parseDouble(Adv);
        probComedy = Double.parseDouble(Comedy);
        probHistory = Double.parseDouble(History);
        probHorror = Double.parseDouble(Horror);
        probDrama = Double.parseDouble(Drama);
        probFantasy = Double.parseDouble(Fantasy);
        probMystery = Double.parseDouble(Mystery);
        probRomance = Double.parseDouble(Romance);
        probScifi = Double.parseDouble(Scifi);
        probThriller = Double.parseDouble(Thriller);
        probWestern = Double.parseDouble(Western);


        Timber.d("Prob action is: %s", probAction);
        Timber.d("Prob adv is: %s", probAdv);
        Timber.d("Prob comedy is: %s", probComedy);
        Timber.d("Prob history is: %s", probHistory);
        Timber.d("Prob horror is: %s", probHorror);
        Timber.d("Prob Drama is: %s", probDrama);
        Timber.d("Prob fantasy is: %s", probFantasy);
        Timber.d("Prob mystery is: %s", probMystery);
        Timber.d("Prob romance is: %s", probRomance);
        Timber.d("Prob scifi is: %s", probScifi);
        Timber.d("Prob thriller is: %s", probThriller);
        Timber.d("Prob western is: %s", probWestern);

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

        Timber.d(finalCategory);
        Timber.d(finaRating);

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
