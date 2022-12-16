package com.ermile.maalquran.android.maalquran.api;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ermile.maalquran.android.QuranApplication;
import com.ermile.maalquran.android.maalquran.LookServer;
import com.ermile.maalquran.android.maalquran.Utility.Json;
import com.ermile.maalquran.android.maalquran.Utility.SaveManager;
import com.ermile.maalquran.android.maalquran.Utility.UserInfo;
import com.ermile.maalquran.android.maalquran.Utility.Url;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Api {

  public static void getToken(final getTokenListener listener) {
    StringRequest request = new StringRequest(Request.Method.POST, Url.getToken(), response -> {
      JSONObject mainObject,result;
      JSONArray msg;
      boolean ok_getToken;
      try {
        mainObject = new JSONObject(response);
        ok_getToken = mainObject.getBoolean("ok");
        if (ok_getToken){
          result = mainObject.getJSONObject("result");
          listener.onReceived(result.getString("token"));
        }else {
          msg = mainObject.getJSONArray("msg");
          for (int i = 0 ; i<= msg.length();i++){
            JSONObject msg_object = msg.getJSONObject(i);
            if (!msg_object.isNull("text")){
              listener.onFailed(msg_object.getString("text"));
            }else {
              listener.onFailed(null);
            }

          }
        }

      } catch (JSONException e) {
        e.printStackTrace();
        listener.onFailed(null);
      }
    }, e -> listener.onFailed(null))
        // Send Headers
    {
      @Override
      public Map<String, String> getHeaders()  {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("appkey", LookServer.appkey);
        return headers;
      }

    };
    request.setRetryPolicy(
        new DefaultRetryPolicy(
            5 * 1000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    QuranApplication.getInstance().addToRequestQueue(request);

  }
  public interface getTokenListener {
    void onReceived(String token);
    void onFailed(String error);
  }

  public static void userAdd(
      final Context context , final String token , final userAddListener listener){
    StringRequest request =
        new StringRequest(Request.Method.POST, Url.getUserAdd(context), response -> {
      JSONObject mainObject,result ;
      JSONArray msg;
      boolean ok_getToken;
      try {
        mainObject = new JSONObject(response);
        ok_getToken = mainObject.getBoolean("ok");
        if (ok_getToken){
          result = mainObject.getJSONObject("result");
          String usercode = result.getString("usercode");
          String zoneid = result.getString("zoneid");
          String apikey = result.getString("apikey");
          SaveManager
              .get(context)
              .save_user_info(apikey,usercode,zoneid, UserInfo.getMobile(context));
          listener.onReceived();
          Log.d("userAdd", "userAdd: "
              + UserInfo.getApikey(context)
              + " | " + UserInfo.getUserCode(context)
              + " | " + UserInfo.getZonId(context)
              + " | " + UserInfo.getMobile(context));
        } else {
          listener.onFailed();
        }

        try {
          msg = mainObject.getJSONArray("msg");
          for (int i = 0 ; i<= msg.length();i++){
            JSONObject object = msg.getJSONObject(i);
            listener.onMassage(object.getString("text"));
          }
        }catch (Exception e){
          e.printStackTrace();
        }

      } catch (JSONException e) {
        e.printStackTrace();
        listener.onFailed();
      }
    }, e -> listener.onFailed())
    {
      @Override
      public Map<String, String> getHeaders()  {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("token", token );
        return headers;
      }

      @Override
      protected Map<String, String> getParams() {
          Map<String,String> device = new HashMap<>();
          device.put("model", Build.MODEL );
          device.put("serial", Build.SERIAL );
          device.put("manufacturer", Build.MANUFACTURER );
          device.put("version", UserInfo.versionName );
          device.put("hardware", Build.HARDWARE );
          device.put("type", Build.TYPE );
          device.put("board", Build.BOARD );
          device.put("id", Build.ID );
          device.put("product", Build.PRODUCT );
          device.put("device", Build.DEVICE );
          device.put("brand", Build.BRAND );
          return device;
      }
    };
    request.setRetryPolicy(
        new DefaultRetryPolicy(
            5 * 1000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    QuranApplication.getInstance().addToRequestQueue(request);
  }
  public interface userAddListener {
    void onReceived();
    void onMassage(String massage);
    void onFailed();
  }

  public static void getAndroidDetail(Context context) {
    StringRequest request =
        new StringRequest(Request.Method.GET, Url.getAndroidDetail(context), response -> {
          try {
            JSONObject mainObject = new JSONObject(response);
            if (mainObject.getBoolean("ok")){
              JSONObject result = mainObject.getJSONObject("result");
              Log.d("amingoli", "getAndroidDetail: "+result);
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

  public static void getLanguageList(Context context) {
    StringRequest request =
        new StringRequest(Request.Method.GET, Url.getLanguageList(context), response -> {
          try {
            SaveManager.get(context).save_json_languageList(response);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }, Throwable::printStackTrace);
    request.setRetryPolicy(new DefaultRetryPolicy(
        2 * 1000,
        1,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    QuranApplication.getInstance().addToRequestQueue(request);
  }

  public static void getAyaDay(Context context) {
    StringRequest request =
        new StringRequest(Request.Method.GET, Url.getAyaDay(context), response -> {
      try {
        if (!Json.Get.ayaDay(context).equals(response)) {
          SaveManager.get(context).save_json_ayaDay(response);
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
  public static void getPageDay(Context context) {
    StringRequest request =
        new StringRequest(Request.Method.GET, Url.getPageDay(context), response -> {
          try {
            if (!Json.Get.pageDay(context).equals(response)) {
              SaveManager.get(context).save_json_pageDay(response);
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
