package com.tarapus.whatmovies.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tarapus.whatmovies.R;
import com.tarapus.whatmovies.network.Movie;

public class MovieAdapter extends BaseAdapter {

    private Context context;
    private Movie[] movies;

    public MovieAdapter(Context context, Movie[] movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Override
    public Movie getItem(int position) {
        return movies[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovieHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);

            holder = new MovieHolder();
            holder.imageIcon = (ImageView) convertView.findViewById(R.id.image_view_movie);
            convertView.setTag(holder);
        } else {
            holder = (MovieHolder) convertView.getTag();
        }

        Movie history = movies[position];

        Picasso.with(this.context).load("http://image.tmdb.org/t/p/w780/" + history.poster_path).into(holder.imageIcon);

        return convertView;
    }

    static class MovieHolder {
        ImageView imageIcon;
    }

    public void addMovies(Movie[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

}
