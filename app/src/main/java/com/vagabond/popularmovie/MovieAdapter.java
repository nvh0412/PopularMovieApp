package com.vagabond.popularmovie;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.model.Constant;
import com.vagabond.popularmovie.model.Movie;

/**
 * Created by HoaNV on 7/19/16.
 */
public class MovieAdapter extends CursorAdapter {
    private final static String LOG_TAG = MovieAdapter.class.getSimpleName();
    private ImageViewHolder mHolder;

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public class ImageViewHolder {
        ImageView imageView;
    }

    private Movie convertCursorToMovie(Cursor cursor) {
        Movie movie = new Movie();
        int idx_poster_path = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movie.setPosterPath(cursor.getString(idx_poster_path));
        return movie;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Movie movie = convertCursorToMovie(cursor);

        mHolder = (ImageViewHolder) view.getTag();
        if (mHolder == null) {
            mHolder = new ImageViewHolder();
            mHolder.imageView = (ImageView) view.findViewById(R.id.list_item_movie_poster);
        }

        Picasso.with(mContext).load(Constant.MOVIEDB_IMAGE_PATH + movie.getPosterPath()).into(mHolder.imageView);
        view.setTag(mHolder);
    }


}
