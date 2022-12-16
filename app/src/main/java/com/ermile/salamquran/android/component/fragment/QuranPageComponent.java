package com.ermile.maalquran.android.component.fragment;

import com.ermile.maalquran.android.di.QuranPageScope;
import com.ermile.maalquran.android.module.fragment.QuranPageModule;
import com.ermile.maalquran.android.ui.fragment.QuranPageFragment;
import com.ermile.maalquran.android.ui.fragment.TabletFragment;
import com.ermile.maalquran.android.ui.fragment.TranslationFragment;

import dagger.Subcomponent;

@QuranPageScope
@Subcomponent(modules = QuranPageModule.class)
public interface QuranPageComponent {
  void inject(QuranPageFragment quranPageFragment);
  void inject(TabletFragment tabletFragment);
  void inject(TranslationFragment translationFragment);

  @Subcomponent.Builder interface Builder {
    Builder withQuranPageModule(QuranPageModule quranPageModule);
    QuranPageComponent build();
  }
}
