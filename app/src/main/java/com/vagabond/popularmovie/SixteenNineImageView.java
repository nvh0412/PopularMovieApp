package com.vagabond.popularmovie;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by HoaNV on 10/19/16.
 */
public class SixteenNineImageView extends ImageView {
  public SixteenNineImageView(Context context) {
    super(context);
  }

  public SixteenNineImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SixteenNineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int height = MeasureSpec.getSize(widthMeasureSpec) * 1;
    int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, heightSpec);
  }
}
