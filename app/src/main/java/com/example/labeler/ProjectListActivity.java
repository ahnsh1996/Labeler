package com.example.labeler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pm10.library.CircleIndicator;

public class ProjectListActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private ProjectDetailAdapter adapter;
    BottomNavigationView bottomNavigationView;
    private final String PREFERENCE = "SECRET";
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectlist);
        setTitle("프로젝트 목록");

        viewPager = (ViewPager) findViewById(R.id.frame);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavi);
        circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        adapter = new ProjectDetailAdapter(getSupportFragmentManager());

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        check();

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch ((item.getItemId())) {
                    case R.id.shop:
                        intent = new Intent(ProjectListActivity.this, ShopActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.require:
                        intent = new Intent(ProjectListActivity.this, WriteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.mypage:
                        intent = new Intent(ProjectListActivity.this, MyPageActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        }); // 하단의 네이게이션 뷰
    }

    public void check() {
        String url = Server.getBaseUrl()+"/board/load/check";
        try {
            Ion.getDefault(ProjectListActivity.this).getConscryptMiddleware().enable(false);
            Ion.with(ProjectListActivity.this)
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(result.get("result").getAsInt() == 1) setting();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // 활성화된 프로젝트들을 로드하기 전 만료된 것이 있는지 서버에 체크하도록 만듦

    public void setting() {
        String url = Server.getBaseUrl()+"/board/load";
        try {
            Ion.getDefault(ProjectListActivity.this).getConscryptMiddleware().enable(false);
            Ion.with(ProjectListActivity.this)
                    .load(url)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            JsonArray projectList = result.getAsJsonArray("project");

                            for(int i=0;i<projectList.size();i++) {
                                JsonObject temp = projectList.get(i).getAsJsonObject();
                                adapter.addItem(new ProjectViewFragment(temp.get("title").getAsString(), temp.get("contenttext").getAsString(), temp.get("closingdate").getAsString(), Math.round(((float)temp.get("CurrentProcess").getAsInt()/(float)temp.get("TotalProcess").getAsInt())*100), temp.get("writer").getAsString()));
                            }

                            viewPager.setAdapter(adapter);
                            circleIndicator.setupWithViewPager(viewPager);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // 프로젝트 정보를 받아와 UI 등 세팅
}