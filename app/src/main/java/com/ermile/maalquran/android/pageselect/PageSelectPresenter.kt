package com.ermile.maalquran.android.pageselect

//import com.crashlytics.android.Crashlytics //1L
import com.ermile.data.source.PageProvider
import com.ermile.maalquran.android.presenter.Presenter
import com.ermile.maalquran.android.util.ImageUtil
import com.ermile.maalquran.android.util.QuranFileUtils
import dagger.Reusable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

@Reusable
class PageSelectPresenter @Inject
    constructor(private val imageUtil: ImageUtil,
                private val quranFileUtils: QuranFileUtils,
                private val mainThreadScheduler: Scheduler,
                private val pageTypes:
                Map<@JvmSuppressWildcards String, @JvmSuppressWildcards PageProvider>) :
    Presenter<PageSelectActivity> {
  private val baseUrl = "https://dl.maalquran.com/app/pagetypes"
  private val compositeDisposable = CompositeDisposable()
  private val downloadingSet = mutableSetOf<String>()
  private var currentView: PageSelectActivity? = null

  private fun generateData() {
    val base = quranFileUtils.quranBaseDirectory
    if (base != null) {
      val outputPath = File(base, "pagetypes")
      if (!outputPath.exists()) {
        outputPath.mkdirs()
        File(outputPath, ".nomedia").createNewFile()
      }

      val data = pageTypes.map {
        val provider = it.value
        val previewImage = File(outputPath, "${it.key}.png")
        val downloadedImage = if (previewImage.exists()) {
          previewImage
        } else if (!downloadingSet.contains(it.key)){
          downloadingSet.add(it.key)
          val url = "$baseUrl/${it.key}.png"
          compositeDisposable.add(
              imageUtil.downloadImage(url, previewImage)
                  .subscribeOn(Schedulers.io())
                  .observeOn(mainThreadScheduler)
                  .subscribe({ generateData() }
//                      , { e -> Crashlytics.logException(e) } //1L
                  )
          )
          null
        } else {
          // already downloading
          null
        }
        PageTypeItem(it.key,
            downloadedImage,
            provider.getPreviewTitle(),
            provider.getPreviewDescription())
      }
      currentView?.onUpdatedData(data)
    }
  }

  override fun bind(what: PageSelectActivity) {
    currentView = what
    generateData()
  }

  override fun unbind(what: PageSelectActivity?) {
    if (currentView === what) {
      currentView = null
      // not clearing the composite disposable to avoid interrupting the download
    }
  }
}
