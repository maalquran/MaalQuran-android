package com.ermile.maalquran.android.dao.translation

data class TranslationHeader(val name: String) : TranslationRowData {

  override fun isSeparator() = true

  override fun name() = this.name

  override fun needsUpgrade() = false
}
