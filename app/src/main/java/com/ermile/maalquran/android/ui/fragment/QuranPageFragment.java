package com.ermile.maalquran.android.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.ermile.maalquran.android.dao.bookmark.Bookmark;
import com.ermile.maalquran.android.data.QuranInfo;
import com.ermile.maalquran.android.module.fragment.QuranPageModule;
import com.ermile.maalquran.android.presenter.quran.QuranPagePresenter;
import com.ermile.maalquran.android.presenter.quran.QuranPageScreen;
import com.ermile.maalquran.android.presenter.quran.ayahtracker.AyahImageTrackerItem;
import com.ermile.maalquran.android.presenter.quran.ayahtracker.AyahScrollableImageTrackerItem;
import com.ermile.maalquran.android.presenter.quran.ayahtracker.AyahTrackerItem;
import com.ermile.maalquran.android.presenter.quran.ayahtracker.AyahTrackerPresenter;
import com.ermile.maalquran.android.ui.PagerActivity;
import com.ermile.maalquran.android.ui.helpers.AyahSelectedListener;
import com.ermile.maalquran.android.ui.helpers.AyahTracker;
import com.ermile.maalquran.android.ui.helpers.HighlightType;
import com.ermile.maalquran.android.ui.helpers.QuranPage;
import com.ermile.maalquran.android.ui.util.PageController;
import com.ermile.maalquran.android.util.QuranScreenInfo;
import com.ermile.maalquran.android.util.QuranSettings;
import com.ermile.maalquran.android.widgets.HighlightingImageView;
import com.ermile.maalquran.android.widgets.QuranImagePageLayout;
import com.ermile.page.common.data.AyahCoordinates;
import com.ermile.page.common.data.PageCoordinates;
import com.ermile.page.common.draw.ImageDrawHelper;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import timber.log.Timber;

import static com.ermile.maalquran.android.ui.helpers.AyahSelectedListener.EventType;

public class QuranPageFragment extends Fragment implements PageController,
    QuranPage, QuranPageScreen, AyahTrackerPresenter.AyahInteractionHandler {
  private static final String PAGE_NUMBER_EXTRA = "pageNumber";

  private int pageNumber;
  private AyahTrackerItem[] ayahTrackerItems;

  @Inject QuranInfo quranInfo;
  @Inject QuranSettings quranSettings;
  @Inject QuranPagePresenter quranPagePresenter;
  @Inject AyahTrackerPresenter ayahTrackerPresenter;
  @Inject AyahSelectedListener ayahSelectedListener;
  @Inject QuranScreenInfo quranScreenInfo;
  @Inject Set<ImageDrawHelper> imageDrawHelpers;

  private HighlightingImageView imageView;
  private QuranImagePageLayout quranPageLayout;
  private boolean ayahCoordinatesError;

  public static QuranPageFragment newInstance(int page) {
    final QuranPageFragment f = new QuranPageFragment();
    final Bundle args = new Bundle();
    args.putInt(PAGE_NUMBER_EXTRA, page);
    f.setArguments(args);
    return f;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onResume() {
    super.onResume();
    updateView();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    final Context context = getActivity();
    quranPageLayout = new QuranImagePageLayout(context);
    quranPageLayout.setPageController(this, pageNumber);
    imageView = quranPageLayout.getImageView();
    return quranPageLayout;
  }

  @Override
  public void updateView() {
    if (isAdded()) {
      quranPageLayout.updateView(quranSettings);
      if (!quranSettings.highlightBookmarks()) {
        imageView.unHighlight(HighlightType.BOOKMARK);
      }
      quranPagePresenter.refresh();
    }
  }

  @Override
  public AyahTracker getAyahTracker() {
    return ayahTrackerPresenter;
  }

  @Override
  public AyahTrackerItem[] getAyahTrackerItems() {
    if (ayahTrackerItems == null) {
      final int height = quranScreenInfo.getHeight();
      ayahTrackerItems = new AyahTrackerItem[]{
        quranPageLayout.canScroll() ?
            new AyahScrollableImageTrackerItem(pageNumber, height,
                quranInfo, quranPageLayout, imageDrawHelpers, imageView) :
            new AyahImageTrackerItem(pageNumber, height, quranInfo, imageDrawHelpers, imageView)
      };
    }
    return ayahTrackerItems;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    pageNumber = getArguments().getInt(PAGE_NUMBER_EXTRA);
    ((PagerActivity) getActivity()).getPagerActivityComponent()
        .quranPageComponentBuilder()
        .withQuranPageModule(new QuranPageModule(pageNumber))
        .build()
        .inject(this);
  }

  @Override
  public void onDetach() {
    ayahSelectedListener = null;
    super.onDetach();
  }

  @Override
  public void onStart() {
    super.onStart();
    quranPagePresenter.bind(this);
    ayahTrackerPresenter.bind(this);
  }

  @Override
  public void onStop() {
    quranPagePresenter.unbind(this);
    ayahTrackerPresenter.unbind(this);
    super.onStop();
  }

  public void cleanup() {
    Timber.d("cleaning up page %d", pageNumber);
    if (quranPageLayout != null) {
      imageView.setImageDrawable(null);
      quranPageLayout = null;
    }
  }

  @Override
  public void setPageCoordinates(PageCoordinates pageCoordinates) {
    ayahTrackerPresenter.setPageBounds(pageCoordinates);
  }

  @Override
  public void setBookmarksOnPage(List<Bookmark> bookmarks) {
    ayahTrackerPresenter.setAyahBookmarks(bookmarks);
  }

  @Override
  public void setAyahCoordinatesData(AyahCoordinates ayahCoordinates) {
    ayahTrackerPresenter.setAyahCoordinates(ayahCoordinates);
    ayahCoordinatesError = false;
  }

  @Override
  public void setAyahCoordinatesError() {
    ayahCoordinatesError = true;
  }

  @Override
  public void onScrollChanged(int x, int y, int oldx, int oldy) {
    PagerActivity activity = (PagerActivity) getActivity();
    if (activity != null) {
      activity.onQuranPageScroll(y);
    }
  }

  @Override
  public void setPageDownloadError(@StringRes int errorMessage) {
    quranPageLayout.showError(errorMessage);
    quranPageLayout.setOnClickListener(v -> ayahSelectedListener.onClick(EventType.SINGLE_TAP));
  }

  @Override
  public void setPageBitmap(int page, @NonNull Bitmap pageBitmap) {
    imageView.setImageDrawable(new BitmapDrawable(getResources(), pageBitmap));
  }

  @Override
  public void hidePageDownloadError() {
    quranPageLayout.hideError();
    quranPageLayout.setOnClickListener(null);
    quranPageLayout.setClickable(false);
  }

  @Override
  public void handleRetryClicked() {
    hidePageDownloadError();
    quranPagePresenter.downloadImages();
  }

  @Override
  public boolean handleTouchEvent(MotionEvent event, EventType eventType, int page) {
    return isVisible() && ayahTrackerPresenter.handleTouchEvent(getActivity(), event, eventType,
        page, ayahSelectedListener, ayahCoordinatesError);
  }
}
