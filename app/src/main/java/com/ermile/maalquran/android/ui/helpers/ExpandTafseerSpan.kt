package com.ermile.maalquran.android.ui.helpers

import android.text.style.ClickableSpan
import android.view.View

class ExpandTafseerSpan(private val listener: View.OnClickListener) : ClickableSpan() {
  override fun onClick(widget: View) {
    listener.onClick(widget)
  }
}
