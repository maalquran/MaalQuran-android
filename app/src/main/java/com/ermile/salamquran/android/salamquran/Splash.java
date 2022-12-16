package com.ermile.maalquran.android.maalquran;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ermile.maalquran.android.QuranApplication;
import com.ermile.maalquran.android.QuranDataActivity;
import com.ermile.maalquran.android.R;
import com.ermile.maalquran.android.maalquran.Intro.IntroActivity;
import com.ermile.maalquran.android.maalquran.Intro.IntroApi;
import com.ermile.maalquran.android.maalquran.Language.LanguageActivity;
import com.ermile.maalquran.android.maalquran.Utility.SaveManager;
import com.ermile.maalquran.android.maalquran.Utility.UserInfo;
import com.ermile.maalquran.android.maalquran.api.UrlApi;
import com.ermile.maalquran.android.maalquran.checkVersion.DeprecatedVersionApi;
import com.ermile.maalquran.android.maalquran.checkVersion.UpdateVersionApi;

import java.util.Locale;

public class Splash extends AppCompatActivity {
  @Override
  protected void onStart() {
    super.onStart();
    try {
      new UrlApi(getApplicationContext());
      new DeprecatedVersionApi(getApplicationContext());
      new UpdateVersionApi(getApplicationContext());
    }catch (Exception e){
      quranActivity();
      Log.e("amingoli", "onStart: ",e );
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    try {
      if (!UserInfo.getDeprecatedVersion(getApplicationContext())){
        switch (UserInfo.getSplash(getApplication())){
          case 0:
            changeLanguage();
            break;
          case 1:
            goIntro();
            break;
          default:
            quranActivity();
            break;
        }
      }else {
        deprecatedDialog();
      }
    }catch (Exception e){
      quranActivity();
      Log.e("amingoli", "onResume: ",e );
    }


  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
  }

  private void changeLanguage() {
    SaveManager.get(getApplication()).save_splash(1);
    String deviceLanguage = Locale.getDefault().getLanguage();
    switch (deviceLanguage){
      case "fa":
      case "ar":
        SaveManager.get(getApplication()).save_app_language(deviceLanguage);
        ((QuranApplication) getApplication()).refreshLocale(this, true);
        goIntro();
        break;
      default:
        Intent intent = new Intent(this, LanguageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        break;
    }
  }
  private void goIntro() {
    new IntroApi(getApplication());
    SaveManager.get(getApplication()).save_splash(2);
    new Handler().postDelayed(() -> {
      Intent intent = new Intent(getApplication(), IntroActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
      startActivity(intent);
      finish();
    },700);

  }
  private void quranActivity() {
    Intent intent = new Intent(this, QuranDataActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    startActivity(intent);
    finish();
  }

  public void deprecatedDialog() {
    try {
      final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
      /*Title*/
      builderSingle.setTitle(getString(R.string.update));
      /*Message*/
      builderSingle.setMessage(getString(R.string.update_warn));
      /*Button*/
      builderSingle.setPositiveButton(getString(R.string.update),
          /*Open Url*/
          (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(UserInfo.getUrlUpdate(getApplicationContext())));
            startActivity(intent);
          });
      builderSingle.setCancelable(false);
      if (!this.isFinishing()){
        builderSingle.show();
      }
    }catch (Exception e){
      quranActivity();
      Log.e("amingoli", "deprecatedDialog: ",e );
    }
  }

}
