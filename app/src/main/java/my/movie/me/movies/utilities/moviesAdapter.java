package my.movie.me.movies.utilities;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import my.movie.me.movies.Items.Movie;
import my.movie.me.movies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        this.favMovies = favoritesArray;
    }

    public void setNumberMovies(int movies) {
        numberMovies = movies;
    }

    public void setMovies(ArrayList<Movie> moviesArray) {
        movies.clear();
        if (movies.isEmpty()) {
        }
        movies = moviesArray;
    }

    public ArrayList<Movie> getFavMovies() {
        return favMovies;
    }

    public void addMovies(ArrayList<Movie> newMoviesArray) {

        int start = newMoviesArray.size() - 100;
        int end = newMoviesArray.size();


        while (start < end) {
            movies.add(newMoviesArray.get(start));
            start++;
        }

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

            int orientation = context.getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                movieItemView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                movieItemView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }

            if (!movies.isEmpty()) {
                String imgURL = context.getString(R.string.API_Img_URL_185) + movies.get(listIndex).getImageURL();

                Movie test = movies.get(listIndex);


                for (Movie a : favMovies) {
                    if (test.getMovieName().equals(a.getMovieName())) {
                        star_yellow.setVisibility(View.VISIBLE);
                    }
                }


                movieItemView.setContentDescription(test.getMovieName());
                Picasso.get()
                        .load(imgURL)
                        .into(movieItemView);
            }
        }
    }


}
