package com.vagabond.popularmovie;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vagabond.popularmovie.data.MovieContract;
import com.vagabond.popularmovie.model.Constant;
import com.vagabond.popularmovie.model.Movie;

/**
 * Created by HoaNV on 7/19/16.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ImageViewHolder> {
  private final static String LOG_TAG = MovieAdapter.class.getSimpleName();

  private Cursor mCursor;
  private Context mContext;
  private MovieAdapterOnClickHandler mClickHandler;

  public MovieAdapter(Context mContext, MovieAdapterOnClickHandler mh) {
    this.mContext = mContext;
    this.mClickHandler = mh;
  }

  public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView imageView;
    TextView titleTV;

    public ImageViewHolder(View itemView) {
      super(itemView);
      imageView = (ImageView) itemView.findViewById(R.id.list_item_movie_poster);
      titleTV = (TextView) itemView.findViewById(R.id.list_item_movie_title);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      mCursor.moveToPosition(adapterPosition);
      mClickHandler.onClick(this);
    }
  }

  private Movie convertCursorToMovie(Cursor cursor) {
    Movie movie = new Movie();
    movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
    movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
    return movie;
  }

  @Override
  public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (parent instanceof RecyclerView) {
      View view = LayoutInflater.from(parent.getContext()).inflate(
        mContext.getResources().getBoolean(R.bool.multi_column)
          ? R.layout.list_item_movie
          : R.layout.list_item_movie, parent, false);
      view.setFocusable(true);
      return new ImageViewHolder(view);
    } else {
      throw new RuntimeException("Not bound to RecyclerViewSelection");
    }
  }

  @Override
  public void onBindViewHolder(ImageViewHolder holder, int position) {
    mCursor.moveToPosition(position);

    Movie movie = convertCursorToMovie(mCursor);

    Picasso.with(mContext).load(Constant.MOVIEDB_BACKDROP_PATH + movie.getPosterPath()).into(holder.imageView);
    holder.titleTV.setText(movie.getTitle());
  }

  @Override
  public int getItemCount() {
    if (null == mCursor) {
      return 0;
    }
    return mCursor.getCount();
  }

  public void swapCursor(Cursor newCursor) {
    mCursor = newCursor;
    notifyDataSetChanged();
  }

  public Cursor getCursor() {
    return mCursor;
  }

  public static interface MovieAdapterOnClickHandler {
    void onClick(ImageViewHolder vh);
  }
}
