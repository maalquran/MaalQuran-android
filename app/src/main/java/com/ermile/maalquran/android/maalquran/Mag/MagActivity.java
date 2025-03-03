package com.ermile.maalquran.android.maalquran.Mag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.ermile.maalquran.android.QuranApplication;
import com.ermile.maalquran.android.R;
import com.ermile.maalquran.android.maalquran.Adapter;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MagActivity extends AppCompatActivity {

  RecyclerViewPager recyclerView;
  Adapter.slider adaptor;
  ArrayList<String> item;
  LinearLayoutManager layoutManager;

  TextView title,subTitle,desc;

  String result = null;
  String image = null;

  @Override
  protected void onResume() {
    super.onResume();
    ((QuranApplication) getApplication()).refreshLocale(this, true);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mag);
    recyclerView = findViewById(R.id.recyclerViewPager);
    item = new ArrayList<>();
    adaptor = new Adapter.slider(getApplicationContext(),item,false);
    recyclerView.setAdapter(adaptor);
    layoutManager =
        new LinearLayoutManager(
            getApplication(),
            LinearLayoutManager.HORIZONTAL,
            false);

    title = findViewById(R.id.title);
    subTitle = findViewById(R.id.short_desc);
    desc = findViewById(R.id.desc);



    if (getIntent().getExtras() != null){
      Intent e = getIntent();
      image = e.getStringExtra("image");
      item.add(image);

      String title_i = e.getStringExtra("title");
      String excerpt = e.getStringExtra("excerpt");
      String content = e.getStringExtra("content");

      title.setText(title_i);
      subTitle.setText(Html.fromHtml(excerpt));
      desc.setText(Html.fromHtml(content));
//    Go in <a> Link html
      subTitle.setMovementMethod(LinkMovementMethod.getInstance());
      desc.setMovementMethod(LinkMovementMethod.getInstance());

      if (e.getStringExtra("gallery") != null){
        result = e.getStringExtra("gallery");
        addItem(result);
      }
      recyclerView.setLayoutManager(layoutManager);
      recyclerView.setItemAnimator(new DefaultItemAnimator());
      adaptor.notifyDataSetChanged();
    }
  }

  private void addItem(String Response){
    try {
      JSONArray array = new JSONArray(Response);
      for (int i = 0; i < array.length(); i++) {
        item.add(array.getString(i));
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
