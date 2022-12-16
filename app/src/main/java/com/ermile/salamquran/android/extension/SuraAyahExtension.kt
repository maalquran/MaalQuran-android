package com.ermile.maalquran.android.extension

import com.ermile.maalquran.android.data.SuraAyah

fun SuraAyah.requiresBasmallah(): Boolean {
  return ayah == 1 && sura != 1 && sura != 9
}
