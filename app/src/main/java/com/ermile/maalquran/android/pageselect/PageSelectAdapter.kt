package com.ermile.maalquran.android.pageselect

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ermile.maalquran.android.R
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference

class PageSelectAdapter(val inflater: LayoutInflater,
                        val width: Int,
                        private val selectionHandler: (String) -> Unit) : androidx.viewpager.widget.PagerAdapter() {
  private val items : MutableList<PageTypeItem> = mutableListOf()
  private val compositeDisposable = CompositeDisposable()

  private val listener = View.OnClickListener { v ->
    val tag = (v.parent as View).tag
    if (tag != null) {
      selectionHandler(tag.toString())
    }
  }

  fun replaceItems(updates: List<PageTypeItem>, pager: androidx.viewpager.widget.ViewPager) {
    items.clear()
    items.addAll(updates)
    items.forEach {
      val view : View? = pager.findViewWithTag(it.pageType)
      if (view != null) {
        updateView(view, it)
      }
    }
    notifyDataSetChanged()
  }

  override fun getCount() = items.size

  override fun isViewFromObject(view: View, obj: Any): Boolean {
    return obj === view
  }

  private fun updateView(view: View, data: PageTypeItem) {
    view.findViewById<TextView>(R.id.title).setText(data.title)
    view.findViewById<TextView>(R.id.description).setText(data.description)
    view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener(listener)

    val image = view.findViewById<ImageView>(R.id.preview)
    if (data.previewImage != null) {
      readImage(data.previewImage.path, WeakReference(image))
    } else {
      image.setImageBitmap(null)
    }
  }

  private fun readImage(path: String, imageRef: WeakReference<ImageView>) {
    compositeDisposable.add(
        Maybe.fromCallable {
          val options = BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ALPHA_8 }
          BitmapFactory.decodeFile(path, options)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe( { imageRef.get()?.setImageBitmap(it) })
    )
  }

  fun cleanUp() {
    compositeDisposable.clear()
  }

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val view = inflater.inflate(R.layout.page_select_page, container, false)
    val item = items[position]
    updateView(view, item)
    view.tag = item.pageType

    container.addView(view)
    return view
  }

  override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
    super.destroyItem(container, position, o)
    container.removeView(o as View)
  }
}
