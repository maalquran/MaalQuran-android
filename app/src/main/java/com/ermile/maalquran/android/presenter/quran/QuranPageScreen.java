package com.ermile.maalquran.android.presenter.quran;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.ermile.maalquran.android.dao.bookmark.Bookmark;
import com.ermile.page.common.data.AyahCoordinates;
import com.ermile.page.common.data.PageCoordinates;

import java.util.List;

public interface QuranPageScreen {
  void setBookmarksOnPage(List<Bookmark> bookmarks);
  void setPageCoordinates(PageCoordinates pageCoordinates);
  void setAyahCoordinatesError();
  void setPageBitmap(int page, @NonNull Bitmap pageBitmap);
  void hidePageDownloadError();
  void setPageDownloadError(@StringRes int errorMessage);
  void setAyahCoordinatesData(AyahCoordinates coordinates);
}
