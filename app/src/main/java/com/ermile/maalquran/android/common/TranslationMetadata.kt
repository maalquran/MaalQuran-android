package com.ermile.maalquran.android.common

import com.ermile.maalquran.android.data.SuraAyah

data class TranslationMetadata(val sura: Int,
                               val ayah: Int,
                               val text: CharSequence,
                               val link: SuraAyah? = null)
