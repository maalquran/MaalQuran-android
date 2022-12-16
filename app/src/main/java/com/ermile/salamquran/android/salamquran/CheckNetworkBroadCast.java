package com.ermile.maalquran.android.maalquran;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ermile.maalquran.android.maalquran.Notification.NotificationSearvic;
import com.ermile.maalquran.android.maalquran.Utility.TempLoginUtil;
import com.ermile.maalquran.android.maalquran.api.Api;

public class CheckNetworkBroadCast extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    context.startService(new Intent(context.getApplicationContext(), NotificationSearvic.class));
    new TempLoginUtil(context);
    /*Api.getAndroidDetail(context);
    Api.getLanguageList(context);
    Api.getAyaDay(context);
    Api.getPageDay(context);*/

  }
}
