package com.example.android.movies.utilities;

import android.content.Context;
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

import com.example.android.movies.Items.Movie;
import com.example.android.movies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class moviesAdapter extends RecyclerView.Adapter<moviesAdapter.NumberViewHolder> {


    private ListItemClickListener onClickListener;
    private ButtonItemClickListener onButtonClickListener;
    private static int viewHolderCount;
    private int numberMovies;
    private ArrayList<Movie> movies;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public interface ButtonItemClickListener {
        void onButtonClick(int clickedItemIndex);
    }

    public moviesAdapter(int movies, ListItemClickListener onclick, ButtonItemClickListener buttonOnClick, ArrayList<Movie> moviesArray) {
        numberMovies = movies;
        onClickListener = onclick;
        onButtonClickListener = buttonOnClick;
        viewHolderCount = 0;
        this.movies = moviesArray;
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
        TextView movieNumber;

        public NumberViewHolder(View itemView) {
            super(itemView);
            movieItemView = (ImageView) itemView.findViewById(R.id.movie_item);
            favButton = (ImageButton) itemView.findViewById(R.id.star_button);
            favButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();

            if (favButton.isPressed()) {
                Log.d("TEST", "ACTIVIATED");
                onButtonClickListener.onButtonClick(clickedPosition);
            } else {
                onClickListener.onListItemClick(clickedPosition);
            }

        }


        void bind(int listIndex) {
            Context context = itemView.getContext();
            if (!movies.isEmpty()) {
                String imgURL = context.getString(R.string.API_Img_URL_185) + movies.get(listIndex).getImageURL();
                Picasso.with(context).load(imgURL).into(movieItemView);
            }
        }
    }


}
