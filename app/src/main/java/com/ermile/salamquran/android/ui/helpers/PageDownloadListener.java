package com.ermile.maalquran.android.ui.helpers;

import android.graphics.drawable.BitmapDrawable;

import com.ermile.maalquran.android.common.Response;

public interface PageDownloadListener {
  void onLoadImageResponse(BitmapDrawable drawable, Response response);
}
