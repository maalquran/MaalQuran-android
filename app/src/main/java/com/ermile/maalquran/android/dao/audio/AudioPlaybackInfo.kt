package com.ermile.maalquran.android.dao.audio

import com.ermile.maalquran.android.data.SuraAyah

data class AudioPlaybackInfo(val currentAyah: SuraAyah,
                             val timesPlayed: Int = 1,
                             val rangePlayedTimes: Int = 1,
                             val shouldPlayBasmallah: Boolean = false)
