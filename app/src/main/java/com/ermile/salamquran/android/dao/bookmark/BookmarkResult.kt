package com.ermile.maalquran.android.dao.bookmark

import com.ermile.maalquran.android.dao.Tag
import com.ermile.maalquran.android.ui.helpers.QuranRow

data class BookmarkResult(val rows: List<QuranRow>, val tagMap: Map<Long, Tag>)
