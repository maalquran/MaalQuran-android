package com.ermile.maalquran.android.maalquran.checkVersion;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ermile.maalquran.android.QuranApplication;
import com.ermile.maalquran.android.maalquran.Utility.SaveManager;
import com.ermile.maalquran.android.maalquran.Utility.Url;
import org.json.JSONObject;

public class UpdateVersionApi {
  public UpdateVersionApi(Context context) {
    StringRequest request =
        new StringRequest(Request.Method.GET, Url.getAndroidDetail(context), response -> {
          try {
            JSONObject mainObject = new JSONObject(response);
            if (mainObject.getBoolean("ok")){
              JSONObject result = mainObject.getJSONObject("result");
              if (!result.isNull("version")){
                JSONObject version = result.getJSONObject("version");
                if (!version.isNull("last")){
                  int last = version.getInt("last");
                  Log.d("amingoli", "UpdateVersionApi: "+last);
                  SaveManager.get(context).save_status_update_version(last);
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }, Throwable::printStackTrace);
    request.setRetryPolicy(new DefaultRetryPolicy(
        5 * 1000,
        0,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    QuranApplication.getInstance().addToRequestQueue(request);
  }
}
