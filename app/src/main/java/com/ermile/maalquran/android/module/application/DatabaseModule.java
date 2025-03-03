package com.ermile.maalquran.android.module.application;

import android.content.Context;

import com.ermile.maalquran.android.data.QuranInfo;
import com.ermile.maalquran.android.database.BookmarksDBAdapter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

  @Provides
  @Singleton
  static BookmarksDBAdapter provideBookmarkDatabaseAdapter(Context context, QuranInfo quranInfo) {
    return new BookmarksDBAdapter(context, quranInfo.getNumberOfPages());
  }
}
