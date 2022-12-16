package com.ermile.maalquran.android.dao.translation

import com.squareup.moshi.Json

data class TranslationList(@field:Json(name = "data") val translations: List<Translation>)
