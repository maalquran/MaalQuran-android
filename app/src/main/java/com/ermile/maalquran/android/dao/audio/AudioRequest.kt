package com.ermile.maalquran.android.dao.audio

import android.os.Parcelable
import com.ermile.maalquran.android.common.QariItem
import com.ermile.maalquran.android.data.SuraAyah
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AudioRequest(val start: SuraAyah,
                        val end: SuraAyah,
                        val qari: QariItem,
                        val repeatInfo: Int = 0,
                        val rangeRepeatInfo: Int = 0,
                        val enforceBounds: Boolean,
                        val shouldStream: Boolean,
                        val audioPathInfo: AudioPathInfo) : Parcelable {
  fun isGapless() = qari.isGapless
  fun needsIsti3athaAudio() =
      !isGapless() || audioPathInfo.gaplessDatabase?.contains("parhizgar_murattal_48") ?: false
}
