package com.ermile.maalquran.android.ui.translation;

import com.ermile.maalquran.android.common.LocalTranslation;
import com.ermile.maalquran.android.common.QuranAyahInfo;

public interface OnTranslationActionListener {
  void onTranslationAction(QuranAyahInfo ayah, LocalTranslation[] translations, int actionId);
}
