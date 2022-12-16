package com.ermile.maalquran.android.module.activity;

import android.content.Context;

import com.ermile.maalquran.android.R;
import com.ermile.maalquran.android.data.QuranInfo;
import com.ermile.maalquran.android.di.ActivityScope;
import com.ermile.maalquran.android.ui.PagerActivity;
import com.ermile.maalquran.android.ui.helpers.AyahSelectedListener;
import com.ermile.maalquran.android.util.QuranScreenInfo;
import com.ermile.maalquran.android.util.QuranUtils;
import com.ermile.maalquran.android.util.TranslationUtil;

import dagger.Module;
import dagger.Provides;

@Module
public class PagerActivityModule {
  private final PagerActivity pagerActivity;

  public PagerActivityModule(PagerActivity pagerActivity) {
    this.pagerActivity = pagerActivity;
  }

  @Provides
  AyahSelectedListener provideAyahSelectedListener() {
    return this.pagerActivity;
  }

  @Provides
  @ActivityScope
  String provideImageWidth(QuranScreenInfo screenInfo) {
    return QuranUtils.isDualPages(pagerActivity, screenInfo) ?
        screenInfo.getTabletWidthParam() : screenInfo.getWidthParam();
  }

  @Provides
  @ActivityScope
  TranslationUtil provideTranslationUtil(Context context, QuranInfo quranInfo) {
    return new TranslationUtil(
        context.getResources().getColor(R.color.translation_translator_color),
        quranInfo);
  }
}
