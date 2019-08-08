package com.example.android.movies.utilities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.movies.AddFavViewModel;
import com.example.android.movies.AddFavViewModelFactory;
import com.example.android.movies.Items.Movie;
import com.example.android.movies.MainViewModel;
import com.example.android.movies.R;
import com.example.android.movies.database.AppDatabase;
import com.example.android.movies.database.FavEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class moviesAdapter extends RecyclerView.Adapter<moviesAdapter.NumberViewHolder> {


    private ListItemClickListener onClickListener;
    private ButtonItemClickListener onButtonClickListener;
    private static int viewHolderCount;
    private int numberMovies;
    private ArrayList<Movie> movies;
    private ArrayList<Movie> favMovies;


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public interface ButtonItemClickListener {
        void onButtonClick(int clickedItemIndex);
    }

    public moviesAdapter(int movies, ListItemClickListener onclick, ButtonItemClickListener buttonOnClick, ArrayList<Movie> moviesArray, ArrayList<Movie> favoritesArray) {
        numberMovies = movies;
        onClickListener = onclick;
        onButtonClickListener = buttonOnClick;
        viewHolderCount = 0;
        this.movies = moviesArray;
        favMovies = favoritesArray;
    }



    @Override
    public NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForMovie = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForMovie, viewGroup, shouldAttachToParentImmediately);
        NumberViewHolder viewHolder = new NumberViewHolder(view);

        viewHolderCount++;
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return numberMovies;
    }

    @Override
    public void onBindViewHolder(NumberViewHolder holder, int i) {
        holder.bind(i);
    }

    class NumberViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        ImageView movieItemView;
        ImageButton favButton;
        ImageView star_white;
        ImageView star_yellow;


        public NumberViewHolder(View itemView) {
            super(itemView);
            movieItemView = (ImageView) itemView.findViewById(R.id.movie_item);
            favButton = (ImageButton) itemView.findViewById(R.id.star_button);
            star_white = (ImageView) itemView.findViewById(R.id.star_background_white);
            star_yellow = (ImageView) itemView.findViewById(R.id.star_background_yellow);
            favButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            if (favButton.isPressed()) {
                Log.d("TEST", "ACTIVIATED");
                if (star_yellow.getVisibility() == View.INVISIBLE) {
                    star_yellow.setVisibility(View.INVISIBLE);
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

                Movie test = movies.get(listIndex);

                for (Movie a : favMovies) {
                    if (test.getMovieName().equals(a.getMovieName())) {
                        Log.d("TEST", "FAVORITE FOUND: " + test.getMovieName());
                        star_yellow.setVisibility(View.VISIBLE);
                    }
                }


                Picasso.with(context).load(imgURL).into(movieItemView);
            }
        }
    }


}
