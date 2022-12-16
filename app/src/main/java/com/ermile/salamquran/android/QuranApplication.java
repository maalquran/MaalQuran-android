package com.ermile.maalquran.android;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

//import com.crashlytics.android.Crashlytics; //1L
//import com.crashlytics.android.core.CrashlyticsCore; //1L
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ermile.maalquran.android.component.application.ApplicationComponent;
import com.ermile.maalquran.android.component.application.DaggerApplicationComponent;
import com.ermile.maalquran.android.module.application.ApplicationModule;
import com.ermile.maalquran.android.maalquran.Utility.UserInfo;
import com.ermile.maalquran.android.util.QuranSettings;

import java.util.Locale;

import androidx.annotation.NonNull;
//import io.fabric.sdk.android.Fabric; //1L


public class QuranApplication extends Application {
  private ApplicationComponent applicationComponent;

  //  maalquran
  public static final String TAG = QuranApplication.class.getSimpleName();
  private RequestQueue mRequestQueue;
  private static QuranApplication mInstance;
//------------------


  @Override
  public void onCreate() {
    super.onCreate();

//  maalquran
    mInstance = this;
//------------------

    //1L
    /*Fabric.with(this, new Crashlytics.Builder()
        .core(new CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build())
        .build());
    Timber.plant(new RecordingLogTree());*/
    //1L
    this.applicationComponent = initializeInjector();
  }

  protected ApplicationComponent initializeInjector() {
    return DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();
  }

  public ApplicationComponent getApplicationComponent() {
    return this.applicationComponent;
  }

  public void refreshLocale(@NonNull Context context, boolean force) {
    final String language = UserInfo.getAppLanguage(this);

    final Locale locale;
    if (language != null) {
      locale = new Locale(language);
//      locale = new Locale("ar");
    } else {
      // nothing to do...
      return;
    }

    updateLocale(context, locale);
    final Context appContext = context.getApplicationContext();
    if (context != appContext) {
      updateLocale(appContext, locale);
    }
  }

  private void updateLocale(@NonNull Context context, @NonNull Locale locale) {
    final Resources resources = context.getResources();
    Configuration config = resources.getConfiguration();
    config.locale = locale;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      config.setLayoutDirection(config.locale);
    }
    resources.updateConfiguration(config, resources.getDisplayMetrics());
  }




  //  maalquran
  public static synchronized QuranApplication getInstance() {
    return mInstance;
  }

  public RequestQueue getRequestQueue() {
    if (mRequestQueue == null) {
      mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }
    return mRequestQueue;
  }
  public <T> void addToRequestQueue(Request<T> req, String tag) {
    // set the default tag if tag is empty
    req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
    getRequestQueue().add(req);
  }

  public <T> void addToRequestQueue(Request<T> req) {
    req.setTag(TAG);
    getRequestQueue().add(req);
  }

  public void cancelPendingRequests(Object tag) {
    if (mRequestQueue != null) {
      mRequestQueue.cancelAll(tag);
    }
  }
//------------------

}
