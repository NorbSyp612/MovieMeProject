package com.example.android.movies.utilities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.movies.Items.Movie;
import com.example.android.movies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import timber.log.Timber;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.NumberViewHolder> {

    private moviesAdapter.ListItemClickListener onClickListener;
    private static int viewHolderCount;
    private int numberMovies;
    private ArrayList<Movie> movies;
    private ArrayList<Movie> favMovies;

    public SearchAdapter(int movies, moviesAdapter.ListItemClickListener onclick, ArrayList<Movie> moviesArray) {
        numberMovies = movies;
        onClickListener = onclick;
        viewHolderCount = 0;
        this.movies = moviesArray;
    }


    @Override
    public SearchAdapter.NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.NumberViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieItemView;
        ImageButton favButton;
        ImageView star_white;
        ImageView star_yellow;


        public NumberViewHolder(View itemView) {
            super(itemView);
            movieItemView = itemView.findViewById(R.id.movie_item);
            favButton = itemView.findViewById(R.id.star_button);
            star_white = itemView.findViewById(R.id.star_background_white);
            star_yellow = itemView.findViewById(R.id.star_background_yellow);
            favButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            if (favButton.isPressed()) {
                Timber.d("ACTIVIATED");
                if (star_yellow.getVisibility() == View.INVISIBLE) {
                    star_yellow.setVisibility(View.VISIBLE);
                } else if (star_yellow.getVisibility() == View.VISIBLE) {
                    star_yellow.setVisibility(View.INVISIBLE);
                }
                onButtonClickListener.onButtonClick(clickedPosition);
            } else {
                onClickListener.onListItemClick(clickedPosition);
            }

        }


        void bind(int listIndex) {
            Context context = itemView.getContext();
            star_yellow.setVisibility(View.INVISIBLE);

            if (!movies.isEmpty()) {
                String imgURL = context.getString(R.string.API_Img_URL_185) + movies.get(listIndex).getImageURL();
                Timber.d(imgURL);

                Movie test = movies.get(listIndex);

                Timber.d("TEST %s", test.getFav());

                for (Movie a : favMovies) {
                    if (test.getMovieName().equals(a.getMovieName())) {
                        Timber.d("FAVORITE FOUND: %s", test.getMovieName());
                        star_yellow.setVisibility(View.VISIBLE);
                    }
                }

                if (test.getFav().equals("yes")) {
                    star_yellow.setVisibility(View.VISIBLE);
                }

                movieItemView.setContentDescription(test.getMovieName());
                Picasso.get()
                        .load(imgURL)
                        .into(movieItemView);
            }
        }
    }

}
