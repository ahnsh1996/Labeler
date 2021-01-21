package com.example.labeler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

// 실제 기능은 미구현
public class ShopActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Button gifticonBtn, withdrawBtn, donateBtn;
    private FragmentManager fm;
    private FragmentTransaction tran;
    private GifticonFrag gifticonFrag;
    private WithdrawFrag withdrawFrag;
    private DonateFrag donateFrag;
    private TextView point;
    private final String PREFERENCE = "SECRET";
    private String jwt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        setTitle("상점");

        gifticonBtn = (Button)findViewById(R.id.gifticon_btn);
        withdrawBtn = (Button)findViewById(R.id.withdraw_btn);
        donateBtn = (Button)findViewById(R.id.donate_btn);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavi);
        bottomNavigationView.setSelectedItemId(R.id.shop);
        gifticonFrag = new GifticonFrag();
        withdrawFrag = new WithdrawFrag();
        donateFrag = new DonateFrag();
        gifticonBtn.setSelected(true);
        setFrag(0);
        point = (TextView)findViewById(R.id.point);

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        setPoint();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch ((item.getItemId())) {
                    case R.id.home:
                        intent = new Intent(ShopActivity.this, ProjectListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.require:
                        intent = new Intent(ShopActivity.this, WriteActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.mypage:
                        intent = new Intent(ShopActivity.this, MyPageActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });

        gifticonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifticonBtn.setSelected(true);
                withdrawBtn.setSelected(false);
                donateBtn.setSelected(false);
                setFrag(0);
            }
        });
        withdrawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifticonBtn.setSelected(false);
                withdrawBtn.setSelected(true);
                donateBtn.setSelected(false);
                setFrag(1);
            }
        });
        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifticonBtn.setSelected(false);
                withdrawBtn.setSelected(false);
                donateBtn.setSelected(true);
                setFrag(2);
            }
        });
    }
    public void setFrag(int n) {
        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();
        switch (n){
            case 0:
                tran.replace(R.id.frag, gifticonFrag);
                tran.commit();
                break;
            case 1:
                tran.replace(R.id.frag, withdrawFrag);
                tran.commit();
                break;
            case 2:
                tran.replace(R.id.frag, donateFrag);
                tran.commit();
                break;
        }
    }

    public void setPoint() {
        String url = Server.getBaseUrl()+"/shop/load";
        try {
            Ion.with(ShopActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result.get("result").getAsInt() == 1)
                                point.setText(Integer.toString(result.get("point").getAsInt()));
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // 상점 기능을 위한 정보(포인트 정보) 로드
}
