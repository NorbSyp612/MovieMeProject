package com.example.android.movies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.movies.R;
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

        genres.add(context.getString(R.string.Action));
        genres.add(context.getString(R.string.Adventure));
        genres.add(context.getString(R.string.Comedy));
        genres.add(context.getString(R.string.History));
        genres.add(context.getString(R.string.Horror));
        genres.add(context.getString(R.string.Drama));
        genres.add(context.getString(R.string.Fantasy));
        genres.add(context.getString(R.string.Mystery));
        genres.add(context.getString(R.string.Romance));
        genres.add(context.getString(R.string.SciFi));
        genres.add(context.getString(R.string.Thriller));
        genres.add(context.getString(R.string.Western));

        ArrayList<String> finalGenreAndRating = new ArrayList<>();

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

        HashMap<String, Double> map = new HashMap<>();

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
        String returnedRating = sharedPreferences.getString(context.getString(R.string.Return_Ratings), "");
        Timber.d(returnedRating);

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

        ratingsTotal =  Double.parseDouble(returnedRating);
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

    private static String getGenreId(String genre) {
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
