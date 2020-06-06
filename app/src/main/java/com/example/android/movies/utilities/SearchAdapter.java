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

import org.w3c.dom.Text;

import java.util.ArrayList;

import timber.log.Timber;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.NumberViewHolder> {

    private ListItemClickListener onClickListener;
    private ButtonItemClickListener onButtonClickListener;
    private static int viewHolderCount;
    private int numberMovies;
    private ArrayList<Movie> movies;

    public SearchAdapter(int movies, ListItemClickListener onclick, ButtonItemClickListener buttonOnClick, ArrayList<Movie> moviesArray) {
        numberMovies = movies;
        onClickListener = onclick;
        onButtonClickListener = buttonOnClick;
        viewHolderCount = 0;
        this.movies = moviesArray;
    }


    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public interface ButtonItemClickListener {
        void onButtonClick(int clickedItemIndex);
    }

    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForMovie = R.layout.search;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForMovie, parent, shouldAttachToParentImmediately);
        SearchAdapter.NumberViewHolder viewHolder = new SearchAdapter.NumberViewHolder(view);
        viewHolderCount++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.NumberViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class NumberViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView searchPoster;
        TextView searchTitle;
        TextView searchDetails;
        TextView searchOverview;
        ImageButton favButton;
        ImageView star_white;
        ImageView star_yellow;

        public NumberViewHolder(View itemView) {
            super(itemView);
            searchPoster = itemView.findViewById(R.id.search_poster);
            searchTitle = itemView.findViewById(R.id.search_title);
            searchDetails = itemView.findViewById(R.id.search_date);
            searchOverview = itemView.findViewById(R.id.search_overview);
            star_white = itemView.findViewById(R.id.search_star_background_white);
            star_yellow = itemView.findViewById(R.id.search_star_background_yellow);
            favButton = itemView.findViewById(R.id.search_star_button);
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

            if (!movies.isEmpty()) {
                String imgURL = context.getString(R.string.API_Img_URL_185) + movies.get(listIndex).getImageURL();
                Timber.d(imgURL);

                Movie test = movies.get(listIndex);

                Timber.d("TEST %s", test.getFav());


                searchPoster.setContentDescription(test.getMovieName());
                Picasso.get()
                        .load(imgURL)
                        .into(searchPoster);

                String title = test.getMovieName();
                String overview = test.getSynopsis();
                String date = test.getReleaseDate();
                String genre = test.getGenre();
                String rating = test.getUserRating();
                String details = date + " - " + genre + " - " + rating + "/10";

                searchTitle.setText(title);
                searchOverview.setText(overview);
                searchDetails.setText(details);
            }
        }
    }

}
