package com.ermile.maalquran.android.maalquran.Utility;

import android.content.Context;

import com.ermile.maalquran.android.BuildConfig;

public class UserInfo {
  public static String versionName = BuildConfig.VERSION_NAME;
  public static int versionCode = BuildConfig.VERSION_CODE;

  public static String getApikey(Context context){
    return SaveManager.get(context).getstring_appINFO().get(SaveManager.apikey);
  }
  public static String getUserCode(Context context){
    return SaveManager.get(context).getstring_appINFO().get(SaveManager.userCode);
  }
  public static String getZonId(Context context){
    return SaveManager.get(context).getstring_appINFO().get(SaveManager.zonId);
  }
  public static String getMobile(Context context){
    return SaveManager.get(context).getstring_appINFO().get(SaveManager.mobile);
  }

  public static Boolean userIdAdded(Context context){
    return getApikey(context) != null || getUserCode(context) != null || getZonId(context) != null;
  }

  public static String getAppLanguage(Context context){
    return SaveManager.get(context).getstring_appINFO().get(SaveManager.appLanguage);
  }

  public static Integer getSplash(Context context){
    return SaveManager.get(context).getIntValue().get(SaveManager.splash);
  }

  public static Boolean getNewVersion(Context context){
    int updateCode = SaveManager.get(context).getIntValue().get(SaveManager.updateVersion);
    return UserInfo.versionCode < updateCode && updateCode != 0;
  }

  public static Boolean getDeprecatedVersion(Context context){
    int deprecatdCode = SaveManager.get(context).getIntValue().get(SaveManager.deprecatedVersion);
    return UserInfo.versionCode <= deprecatdCode && deprecatdCode != 0;
  }

  public static String getUrlUpdate(Context context){
    return SaveManager.get(context).getstring_appINFO().get(SaveManager.url_update);
  }
}
