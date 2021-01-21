package com.example.labeler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyPageActivity extends AppCompatActivity {
    private TextView id, birth, phone, point;
    private Button joinedProject, myProject;
    private ListView joinedProjectView, myProjectView;
    private ArrayList<String> joinedProjectList;
    //private ArrayList<String> myProjectList;
    private ArrayAdapter joinedProjectAdapter;
    //private ArrayAdapter myProjectAdapter;
    private CustomAdapter myProjectAdapter;
    private BottomNavigationView bottomNavigationView;
    private String jwt;
    private final String PREFERENCE = "SECRET";
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setTitle("마이 페이지");

        id = (TextView)findViewById(R.id.id);
        birth = (TextView)findViewById(R.id.birth);
        phone = (TextView)findViewById(R.id.phone);
        point = (TextView)findViewById(R.id.point);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        joinedProject = (Button)findViewById(R.id.joinedProject);
        myProject = (Button)findViewById(R.id.myProject);
        joinedProjectView = (ListView)findViewById(R.id.joinedProjectList);
        myProjectView = (ListView)findViewById(R.id.myProjectList);
        joinedProjectList = new ArrayList<String>();
        //myProjectList = new ArrayList<String>();
        joinedProjectAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, joinedProjectList);
        //myProjectAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, myProjectList);
        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");
        myProjectAdapter = new CustomAdapter(getApplicationContext(), jwt, progressBar);
        joinedProjectView.setAdapter(joinedProjectAdapter);
        myProjectView.setAdapter(myProjectAdapter);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavi);
        bottomNavigationView.setSelectedItemId(R.id.mypage);

        setting();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch ((item.getItemId())){
                    case R.id.home:
                        intent = new Intent(MyPageActivity.this, ProjectListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.require:
                        intent = new Intent(MyPageActivity.this, WriteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.shop:
                        intent = new Intent(MyPageActivity.this, ShopActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });

        joinedProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joinedProjectView.getVisibility() == View.GONE) {
                    joinedProjectView.setVisibility(View.VISIBLE);
                    if(joinedProjectList.isEmpty()) {
                        joinedProjectList.add("참여한 프로젝트가 없습니다.");
                        joinedProjectAdapter.notifyDataSetChanged();
                    }
                } else
                    joinedProjectView.setVisibility(View.GONE);
            }
        }); // 참여한 프로젝트 현황을 보여줌
        myProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myProjectView.getVisibility() == View.GONE) {
                    myProjectView.setVisibility(View.VISIBLE);
                    if(myProjectAdapter.getCount() == 0) {
                        myProjectAdapter.addItem(new CustomData("의뢰한 프로젝트가 없습니다.",1));
                        myProjectAdapter.notifyDataSetChanged();
                    }
                } else
                    myProjectView.setVisibility(View.GONE);
            }
        }); // 의뢰한 프로젝트 현황을 보여줌
    }

    public void setting() {
        String url = Server.getBaseUrl()+"/user/mypage";
        try {
            Ion.with(MyPageActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result.get("result").getAsInt() == 1) {
                                JsonObject temp = result.get("user").getAsJsonObject();
                                id.setText(temp.get("accountid").getAsString());
                                birth.setText(temp.get("birth").getAsString());
                                phone.setText(temp.get("phonenumber").getAsString());
                                point.setText(temp.get("point").getAsInt()+"");

                                JsonArray joinedlist = temp.get("joinedproject").getAsJsonArray();
                                for(int i=0;i<joinedlist.size();i++)
                                    joinedProjectList.add(joinedlist.get(i).getAsString());
                                joinedProjectAdapter.notifyDataSetChanged();

                                JsonArray myproject = temp.get("myproject").getAsJsonObject().get("project").getAsJsonArray();
                                JsonArray myprojectActive = temp.get("myproject").getAsJsonObject().get("active").getAsJsonArray();

                                for(int i=0;i<myproject.size();i++) {
                                    if(myprojectActive.get(i).getAsInt() == 1)
                                        myProjectAdapter.addItem(new CustomData(myproject.get(i).getAsString(), 1));
                                    else
                                        myProjectAdapter.addItem(new CustomData(myproject.get(i).getAsString(), 0));
                                }
                                myProjectAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // 마이 페이지 로딩을 위한 정보를 가져옴
}
