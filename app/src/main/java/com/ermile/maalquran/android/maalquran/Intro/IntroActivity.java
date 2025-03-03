package com.ermile.maalquran.android.maalquran.Intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.ermile.maalquran.android.QuranDataActivity;
import com.ermile.maalquran.android.R;
import com.ermile.maalquran.android.maalquran.Utility.Json;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

  List<IntroModel> itemIntroList;
  RecyclerViewPager recyclerViewPager;
  IntroAdapter adaptorIntro;

  Button nex, prav;
  String nex_string, pravs_string, skip_string;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_intro);

    nex_string = "->";
    pravs_string = "<-";
    skip_string = "SKIP";

    nex = findViewById(R.id.btn_next);
    prav = findViewById(R.id.btn_prav);



    itemIntroList = new ArrayList<>();
    recyclerViewPager  = findViewById(R.id.recyclerViewPager_intro);
    adaptorIntro  = new IntroAdapter(this,itemIntroList);
    recyclerViewPager.setAdapter(adaptorIntro);
    final LinearLayoutManager layout = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);


    try {
      JSONArray intro = new JSONArray( Json.Get.intro(getApplication()) );

      for (int i = 0; i < intro.length(); i++) {
        JSONObject object = intro.getJSONObject(i);
        String image= null;
        if (!object.isNull("image")){
          image = object.getString("image");
        }
        String title = object.getString("title");
        String desc = object.getString("desc");

        String bg_from = object.getString("bg_from");
        String bg_to = object.getString("bg_to");
        String title_color = object.getString("title_color");
        String desc_color = object.getString("desc_color");

        itemIntroList.add(new IntroModel(image,title,desc,bg_from,bg_to,title_color,desc_color));
        recyclerViewPager.setLayoutManager(layout);
        recyclerViewPager.setItemAnimator(new DefaultItemAnimator());


        if (!object.isNull("btn")){
          JSONArray btnArray = object.getJSONArray("btn");
          for (int j = 0; j < btnArray.length(); j++) {
            JSONObject btnObject = btnArray.getJSONObject(j);
            String action = btnObject.getString("action");
            String titleBtn = btnObject.getString("title");

            switch (action){
              case "next":
                nex_string = titleBtn;
                break;
              case "prev":
                pravs_string = titleBtn;
                break;
              case "start":
                skip_string = titleBtn;
                break;
            }
          }
        }
      }

      if (itemIntroList.size() == 1){
        nex.setText(nex_string);
        nex.setOnClickListener(view -> {
          finish();
          startActivity(new Intent(getApplicationContext(), QuranDataActivity.class));
        });
      }
      else {
        nex.setText(nex_string);
        prav.setText(pravs_string);
        nex.setOnClickListener(view -> {

          if (page_intro() == itemIntroList.size()-1){
            finish();
            startActivity(new Intent(getApplicationContext(),QuranDataActivity.class));
          }else {
            recyclerViewPager.smoothScrollToPosition(recyclerViewPager.getCurrentPosition() + 1);
          }
        });
        prav.setOnClickListener(view ->
            recyclerViewPager.smoothScrollToPosition(recyclerViewPager.getCurrentPosition() - 1));

        recyclerViewPager.addOnPageChangedListener((i, i1) -> {
          if (recyclerViewPager.isScrollContainer()){
            if (page_intro() == itemIntroList.size()-1){
              nex.setText(skip_string);
            }else {
              nex.setText(nex_string);
            }

            if (page_intro() >=1){
              prav.setVisibility(View.VISIBLE);
            }else {
              prav.setVisibility(View.INVISIBLE);
            }
          }
        });
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private int page_intro(){
    return recyclerViewPager.getCurrentPosition();
  }


}
