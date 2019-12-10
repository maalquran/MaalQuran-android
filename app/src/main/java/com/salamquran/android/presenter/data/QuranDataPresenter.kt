package com.salamquran.android.presenter.data

import android.content.Context
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
//import com.crashlytics.android.Crashlytics //1L
import com.ermile.data.source.PageProvider
import com.salamquran.android.BuildConfig
import com.salamquran.android.QuranDataActivity
import com.salamquran.android.data.QuranDataProvider
import com.salamquran.android.data.QuranInfo
import com.salamquran.android.presenter.Presenter
import com.salamquran.android.util.CopyDatabaseUtil
import com.salamquran.android.util.QuranFileUtils
import com.salamquran.android.util.QuranScreenInfo
import com.salamquran.android.util.QuranSettings
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class QuranDataPresenter @Inject internal constructor(
    val appContext: Context,
    val quranInfo: QuranInfo,
    val quranScreenInfo: QuranScreenInfo,
    private val quranPageProvider: PageProvider,
    private val copyDatabaseUtil: CopyDatabaseUtil,
    val quranFileUtils: QuranFileUtils) : Presenter<QuranDataActivity> {

  private var activity: QuranDataActivity? = null
  private var checkPagesDisposable: Disposable? = null
  private var cachedPageType: String? = null
  private var lastCachedResult: QuranDataStatus? = null

  private var debugLog: String? = null

  private val quranSettings = QuranSettings.getInstance(appContext)

  @UiThread
  fun checkPages() {
    if (quranFileUtils.getQuranBaseDirectory(appContext) == null) {
      activity?.onStorageNotAvailable()
    } else if (lastCachedResult != null && cachedPageType == quranSettings.pageType) {
      activity?.onPagesChecked(lastCachedResult)
    } else if (checkPagesDisposable == null) {
      val pages = quranInfo.numberOfPages
      val pageType = quranSettings.pageType
      checkPagesDisposable =
          supportLegacyPages(pages)
              .andThen(actuallyCheckPages(pages))
              .map { checkPatchStatus(it) }
              .doOnSuccess {
                if (it.havePages()) {
                  copyArabicDatabaseIfNecessary()
                } else {
                  try {
                    generateDebugLog()
                  } catch (e: Exception) {
//                    Crashlytics.logException(e) //1L
                  }
                }
              }
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(Consumer<QuranDataStatus> {
                if (it.havePages() && it.patchParam == null) {
                  // only cache cases where no downloads are needed - otherwise, caching
                  // is risky since after coming back to the app, the downloads could be
                  // done, though the cached data suggests otherwise.
                  cachedPageType = pageType
                  lastCachedResult = it
                }
                activity?.onPagesChecked(it)
                checkPagesDisposable = null
              })
    }
  }

  fun getDebugLog(): String = debugLog ?: ""

  private fun generateDebugLog() {
    val directory = quranFileUtils.quranBaseDirectory
    directory?.let {
      val log = StringBuilder()

      val quranImagesDirectoryName = quranFileUtils.getQuranImagesBaseDirectory(appContext)
      val quranImagesDirectory = File(quranImagesDirectoryName)
      val quranImagesDirectoryFiles = quranImagesDirectory.listFiles()
      quranImagesDirectoryFiles?.let { files ->
        val imageSubdirectories = files.filter { it.name.contains("width_") }
        imageSubdirectories.map {
          log.append("image directory: ")
              .append(it.name)
              .append(" - ")
          val imageFiles = it.listFiles()
          imageFiles?.let { images ->
            val fileCount = images.size
            log.append(fileCount)

            if (fileCount == 1) {
              log.append(" [")
                  .append(images[0].name)
                  .append("]")
            }
          }
          log.append("\n")

          if (imageFiles == null) {
            log.append("null image file list, $it - ${it.isDirectory}")
          }
        }
      }

      if (quranImagesDirectoryFiles == null) {
        log.append("null list of files in images directory: $quranImagesDirectoryName - ${quranImagesDirectory.isDirectory}")
      }

      val audioDirectory = quranFileUtils.getQuranAudioDirectory(appContext)
      log.append("audio files in audio root: ")
         .append(File(audioDirectory).listFiles()?.size ?: "null")
      debugLog = log.toString()
    }

    if (directory == null) {
      debugLog = "can't find quranBaseDirectory"
    }
  }

  private fun supportLegacyPages(totalPages: Int): Completable {
    return Completable.fromCallable {
      if (!quranSettings.haveDefaultImagesDirectory() && "madani" == quranSettings.pageType) {
        /* this code is only valid for the legacy madani pages.
         *
         * previously, we would send any screen widths greater than 1280
         * to get 1920 images. this was problematic for various reasons,
         * including:
         * a. a texture limit for the maximum size of a bitmap that could
         *    be loaded, which the 1920x3106 images exceeded on devices
         *    with the minimum 2048 height capacity.
         * b. slow to switch pages due to the massive size of the gl
         *    texture loaded by android.
         *
         * consequently, in this new version, we make anything above 1024
         * fallback to a 1260 bucket (height of 2038). this works around
         * both problems (much faster page flipping now too) with a very
         * minor loss in quality.
         *
         * this code checks and sees, if the user already has a complete
         * folder of 1920 images, in which case it sets that in the pref
         * so we load those instead of 1260s.
         */
        val fallback = quranFileUtils.getPotentialFallbackDirectory(appContext, totalPages)
        if (fallback != null) {
          Timber.d("setting fallback pages to %s", fallback)
          quranSettings.setDefaultImagesDirectory(fallback)
        } else {
          // stop doing this check every launch if the images don't exist.
          // since the method checks for the key being missing (and since
          // override parameter ignores the empty string), empty string is
          // fine here.
          quranSettings.setDefaultImagesDirectory("")
        }
      }
    }
  }

  private fun actuallyCheckPages(totalPages: Int): Single<QuranDataStatus> {
    return Single.fromCallable<QuranDataStatus> {
      val width = quranScreenInfo.widthParam
      val havePortrait = quranFileUtils.haveAllImages(appContext, width, totalPages, true)

      val tabletWidth = quranScreenInfo.tabletWidthParam
      val needLandscapeImages = if (quranScreenInfo.isDualPageMode && width != tabletWidth) {
        val haveLandscape = quranFileUtils.haveAllImages(appContext, tabletWidth, totalPages, true)
        Timber.d("checkPages: have portrait images: %s, have landscape images: %s",
            if (havePortrait) "yes" else "no", if (haveLandscape) "yes" else "no")
        !haveLandscape
      } else {
        // either not dual screen mode or the widths are the same
        Timber.d("checkPages: have all images: %s", if (havePortrait) "yes" else "no")
        false
      }

      QuranDataStatus(width, tabletWidth, havePortrait, !needLandscapeImages, null)
    }
  }

  @WorkerThread
  private fun checkPatchStatus(quranDataStatus: QuranDataStatus): QuranDataStatus {
    // only need patches if we have all the pages
    if (quranDataStatus.havePages()) {
      val latestImagesVersion = quranPageProvider.getImageVersion()

      val width = quranDataStatus.portraitWidth
      val needPortraitPatch =
          !quranFileUtils.isVersion(appContext, width, latestImagesVersion)

      val tabletWidth = quranDataStatus.landscapeWidth
      if (width != tabletWidth) {
        val needLandscapePatch =
            !quranFileUtils.isVersion(appContext, tabletWidth, latestImagesVersion)
        if (needLandscapePatch) {
          return quranDataStatus.copy(patchParam = width + tabletWidth)
        }
      }

      if (needPortraitPatch) {
        return quranDataStatus.copy(patchParam = width)
      }
    }
    return quranDataStatus
  }

  @WorkerThread
  private fun copyArabicDatabaseIfNecessary() {
    // in 2.9.1 and above, the Arabic databases were renamed. To make it easier for
    // people to get them, the app will bundle them with the apk for a few releases.
    // if the database doesn't exist, let's try to copy it if we can. Only check this
    // if we have all the files, since if not, they come bundled with the full pages
    // zip file anyway. Note that, for now, this only applies for the madani app.
    if ("madani" == BuildConfig.FLAVOR && !quranFileUtils.hasArabicSearchDatabase(appContext)) {
      val success = copyDatabaseUtil.copyArabicDatabaseFromAssets(QuranDataProvider.QURAN_ARABIC_DATABASE)
          .blockingGet()
      if (success) {
        Timber.d("copied Arabic database successfully")
      } else {
        Timber.d("failed to copy Arabic database")
      }
    }
  }

  override fun bind(activity: QuranDataActivity) {
    this.activity = activity
  }

  override fun unbind(activity: QuranDataActivity) {
    if (this.activity === activity) {
      this.activity = null
    }
  }

  data class QuranDataStatus(val portraitWidth: String,
                             val landscapeWidth: String,
                             val havePortrait: Boolean,
                             val haveLandscape: Boolean,
                             val patchParam: String?) {
    fun needPortrait() = !havePortrait
    fun needLandscape() = !haveLandscape
    fun havePages() = havePortrait && haveLandscape
  }
}
