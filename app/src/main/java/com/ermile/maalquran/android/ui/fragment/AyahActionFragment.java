package com.ermile.maalquran.android.ui.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.ermile.maalquran.android.data.SuraAyah;
import com.ermile.maalquran.android.ui.PagerActivity;

public abstract class AyahActionFragment extends Fragment {

  protected SuraAyah start;
  protected SuraAyah end;
  private boolean justCreated;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    justCreated = true;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (justCreated) {
      justCreated = false;
      PagerActivity activity = (PagerActivity) getActivity();
      if (activity != null) {
        start = activity.getSelectionStart();
        end = activity.getSelectionEnd();
        refreshView();
      }
    }
  }

  public void updateAyahSelection(SuraAyah start, SuraAyah end) {
    this.start = start;
    this.end = end;
    refreshView();
  }

  protected abstract void refreshView();

}
