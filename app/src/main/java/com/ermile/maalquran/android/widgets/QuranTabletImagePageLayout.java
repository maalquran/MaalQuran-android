package com.ermile.maalquran.android.widgets;

import android.content.Context;

public class QuranTabletImagePageLayout extends QuranImagePageLayout {

  public QuranTabletImagePageLayout(Context context) {
    super(context);
  }

  @Override
  protected boolean shouldWrapWithScrollView() {
    return false;
  }
}
