package com.example.davenguyen.lomoapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dave Nguyen on 15-Oct-17.
 * This class is used for displaying the result
 */

public class ResultMovieListViewAdapter extends BaseAdapter implements Filterable {
    // Declare Variables
    private AppCompatActivity activity;
    private MovieFilter movieFilter;
    private ArrayList<Movie> movieList;
    private ArrayList<Movie> filteredList;

    private Filter.FilterListener filterListener;

    /**
     * Initialize context variables
     *
     * @param activity
     * @param movieList movie list
     */
    public ResultMovieListViewAdapter(AppCompatActivity activity, ArrayList<Movie> movieList) {
        this.activity = activity;
        this.movieList = movieList;
        this.filteredList = movieList;

        getFilter();
    }

    /**
     * Get size of movie list
     *
     * @return movieList size
     */
    @Override
    public int getCount() {
        return filteredList.size();
    }

    /**
     * Get specific item from movie list
     *
     * @param i item index
     * @return list item
     */
    @Override
    public Movie getItem(int i) {
        return filteredList.get(i);
    }

    /**
     * Get movie list item id
     *
     * @param i item index
     * @return current item id
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Create list row view
     *
     * @param position index
     * @param view     current list item view
     * @param parent   parent
     * @return view
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        final ViewHolder holder;
        final Movie movie = (Movie) getItem(position);

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.result_movie_list_row, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.rmlr_name);
            holder.year = (TextView) view.findViewById(R.id.rmlr_year);
            holder.image = view.findViewById(R.id.rmlr_img);


            view.setTag(holder);
        } else {
            // get view holder back
            holder = (ViewHolder) view.getTag();
        }

        // bind text with view holder content view for efficient use
        holder.name.setText(movie.getName());
        holder.year.setText(movie.getYear());
        Picasso.with(activity).load(movie.getImage()).into(holder.image);

        return view;
    }


    /**
     * Get custom filter
     *
     * @return filter
     */
    @Override
    public Filter getFilter() {
        if (movieFilter == null) {
            movieFilter = new MovieFilter();
        }

        return movieFilter;
    }
    public void setFilterListener(Filter.FilterListener filterListener)
    {
        this.filterListener = filterListener;
    }

    /**
     * Keep reference to children view to avoid unnecessary calls
     */
    static class ViewHolder {
        TextView name;
        TextView year;
        ImageView image;
    }

    /**
     * Custom filter for movie list
     * Filter content in movie list according to the search text
     */
    private class MovieFilter extends Filter {
        private Integer count;
        @Override
        protected FilterResults performFiltering(CharSequence character) {
            FilterResults filterResults = new FilterResults();
            if (character != null && character.length() > 0) {
                ArrayList<Movie> tempList = new ArrayList<Movie>();

                // search content in friend list
                for (Movie user : movieList) {
                    //Searching for the appropriate character
                    if (user.getName().toLowerCase().contains(character.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {


                filterResults.count = movieList.size();
                filterResults.values = movieList;
            }
            count = filterResults.count;
            return filterResults;
        }



        /**
         * Notify about filtered list to ui
         *
         * @param constraint text
         * @param results    filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(filterListener!=null)
                filterListener.onFilterComplete(count);

            filteredList = (ArrayList<Movie>) results.values;
            notifyDataSetChanged();
        }

    }
}
