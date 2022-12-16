package com.ermile.maalquran.android.component.activity;

import com.ermile.maalquran.android.component.fragment.QuranPageComponent;
import com.ermile.maalquran.android.di.ActivityScope;
import com.ermile.maalquran.android.module.activity.PagerActivityModule;
import com.ermile.maalquran.android.ui.PagerActivity;
import com.ermile.maalquran.android.ui.fragment.AyahTranslationFragment;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = PagerActivityModule.class)
public interface PagerActivityComponent {
  // subcomponents
  QuranPageComponent.Builder quranPageComponentBuilder();

  void inject(PagerActivity pagerActivity);
  void inject(AyahTranslationFragment ayahTranslationFragment);

  @Subcomponent.Builder interface Builder {
    Builder withPagerActivityModule(PagerActivityModule pagerModule);
    PagerActivityComponent build();
  }
}
