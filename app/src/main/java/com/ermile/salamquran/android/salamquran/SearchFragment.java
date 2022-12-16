package com.ermile.maalquran.android.maalquran;


import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.ermile.maalquran.android.QuranApplication;
import com.ermile.maalquran.android.R;
import com.ermile.maalquran.android.SearchActivity;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

  public SearchFragment() {
    // Required empty public constructor
  }

  @Override
  public void onResume() {
    super.onResume();
    ((QuranApplication)
        Objects.requireNonNull(getActivity()).getApplication())
        .refreshLocale(Objects.requireNonNull(getContext()), true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_search, container, false);
    SearchView searchView = view.findViewById(R.id.searchView);
    searchView.setQueryHint(getString(R.string.search_hint));
    SearchManager searchManager = (SearchManager)
        Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
    searchView.setSearchableInfo(searchManager.getSearchableInfo(
        new ComponentName(getActivity().getApplication(), SearchActivity.class)));
    return view;
  }

}
