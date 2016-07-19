package com.vagabond.popularmovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vagabond.popularmovie.model.Constant;
import com.vagabond.popularmovie.model.Movie;

import java.util.List;

/**
 * Created by HoaNV on 7/19/16.
 */
public class MovieAdapter extends BaseAdapter {
    private final static String LOG_TAG = MovieAdapter.class.getSimpleName();
    private List<Movie> movieList;
    private Context mContext;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.movieList = movieList;
        this.mContext = context;
    }

    public void clear() {
        movieList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int position) {
        return movieList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movieList.get(position).getId();
    }

    public void addAll(List<Movie> movieList) {
        this.movieList.addAll(movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = movieList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_movie, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_movie_poster);
        Picasso.with(mContext).load(Constant.MOVIEDB_IMAGE_PATH + movie.getPosterPath()).into(imageView);

        return convertView;
    }
}
