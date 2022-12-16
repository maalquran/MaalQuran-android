package com.ermile.maalquran.android.dao.bookmark

import com.ermile.maalquran.android.dao.RecentPage
import com.ermile.maalquran.android.dao.Tag

data class BookmarkData(val tags: List<Tag> = emptyList(),
                        val bookmarks: List<Bookmark> = emptyList(),
                        val recentPages: List<RecentPage> = emptyList())
