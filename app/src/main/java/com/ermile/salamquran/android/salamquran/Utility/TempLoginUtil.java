package com.ermile.maalquran.android.maalquran.Utility;

import android.content.Context;

import com.ermile.maalquran.android.maalquran.api.Api;

public class TempLoginUtil {
  private Context context;
  public TempLoginUtil(Context context) {
    this.context = context;
    try {
      if (!UserInfo.userIdAdded(context)){
        Api.getToken(new Api.getTokenListener() {
          @Override
          public void onReceived(String token) {
            Api.userAdd(context, token,
                new Api.userAddListener() {
                  @Override
                  public void onReceived() {}
                  @Override
                  public void onMassage(String massage) {}
                  @Override
                  public void onFailed() {}
                });
          }
          @Override
          public void onFailed(String error) {}
        });
      }
    }catch (Exception e){
      e.printStackTrace();
    }

  }
}
