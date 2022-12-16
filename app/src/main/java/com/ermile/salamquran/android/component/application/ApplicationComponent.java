package com.ermile.maalquran.android.component.application;

import com.ermile.data.page.provider.QuranPageModule;
import com.ermile.maalquran.android.QuranDataActivity;
import com.ermile.maalquran.android.QuranForwarderActivity;
import com.ermile.maalquran.android.QuranImportActivity;
import com.ermile.maalquran.android.SearchActivity;
import com.ermile.maalquran.android.component.activity.PagerActivityComponent;
import com.ermile.maalquran.android.data.QuranDataModule;
import com.ermile.maalquran.android.data.QuranDataProvider;
import com.ermile.maalquran.android.module.application.ApplicationModule;
import com.ermile.maalquran.android.module.application.DatabaseModule;
import com.ermile.common.networking.NetworkModule;
import com.ermile.maalquran.android.pageselect.PageSelectActivity;
import com.ermile.maalquran.android.service.AudioService;
import com.ermile.maalquran.android.service.QuranDownloadService;
import com.ermile.maalquran.android.ui.AudioManagerActivity;
import com.ermile.maalquran.android.ui.QuranActivity;
import com.ermile.maalquran.android.ui.TranslationManagerActivity;
import com.ermile.maalquran.android.ui.fragment.AddTagDialog;
import com.ermile.maalquran.android.ui.fragment.AyahPlaybackFragment;
import com.ermile.maalquran.android.ui.fragment.BookmarksFragment;
import com.ermile.maalquran.android.ui.fragment.JumpFragment;
import com.ermile.maalquran.android.ui.fragment.JuzListFragment;
import com.ermile.maalquran.android.ui.fragment.QuranAdvancedSettingsFragment;
import com.ermile.maalquran.android.ui.fragment.QuranSettingsFragment;
import com.ermile.maalquran.android.ui.fragment.SuraListFragment;
import com.ermile.maalquran.android.ui.fragment.TagBookmarkDialog;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
    ApplicationModule.class,
    DatabaseModule.class,
    NetworkModule.class,
    QuranDataModule.class,
    QuranPageModule.class } )
public interface ApplicationComponent {
  // subcomponents
  PagerActivityComponent.Builder pagerActivityComponentBuilder();

  // content provider
  void inject(QuranDataProvider quranDataProvider);

  // services
  void inject(AudioService audioService);
  void inject(QuranDownloadService quranDownloadService);

  // activities
  void inject(QuranActivity quranActivity);
  void inject(QuranDataActivity quranDataActivity);
  void inject(QuranImportActivity quranImportActivity);
  void inject(AudioManagerActivity audioManagerActivity);
  void inject(QuranForwarderActivity quranForwarderActivity);
  void inject(SearchActivity searchActivity);
  void inject(PageSelectActivity pageSelectActivity);

  // fragments
  void inject(BookmarksFragment bookmarksFragment);
  void inject(QuranSettingsFragment fragment);
  void inject(TranslationManagerActivity translationManagerActivity);
  void inject(QuranAdvancedSettingsFragment quranAdvancedSettingsFragment);
  void inject(SuraListFragment suraListFragment);
  void inject(JuzListFragment juzListFragment);
  void inject(AyahPlaybackFragment ayahPlaybackFragment);
  void inject(JumpFragment jumpFragment);

  // dialogs
  void inject(TagBookmarkDialog tagBookmarkDialog);
  void inject(AddTagDialog addTagDialog);
}
