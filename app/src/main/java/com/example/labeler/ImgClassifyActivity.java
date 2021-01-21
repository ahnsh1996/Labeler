package com.example.labeler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class ImgClassifyActivity extends AppCompatActivity {
    private ImageButton ib[];
    private boolean check[];
    private Button btn;
    private TextView tv;
    private LinearLayout Container[];
    private String writer, title, curObj;
    private JsonArray curList;
    private final String PREFERENCE = "SECRET";
    private String jwt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_classify);
        setTitle("이미지 분류");

        initialization();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult();
            }
        });
        ib[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check(0);
            }
        });
        ib[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check(1);
            }
        });
        ib[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check(2);
            }
        });
        ib[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check(3);
            }
        });
    }
    public void initialization(){
        Intent intent = getIntent();

        writer = intent.getExtras().getString("writer");
        title = intent.getExtras().getString("title");
        ib = new ImageButton[4];
        ib[0] = (ImageButton)findViewById(R.id.imageButton1);
        ib[1] = (ImageButton)findViewById(R.id.imageButton2);
        ib[2] = (ImageButton)findViewById(R.id.imageButton3);
        ib[3] = (ImageButton)findViewById(R.id.imageButton4);
        check = new boolean[4];
        btn = (Button)findViewById(R.id.button);
        tv = (TextView)findViewById(R.id.textView);
        Container = new LinearLayout[4];
        Container[0] = (LinearLayout)findViewById(R.id.Container1);
        Container[1] = (LinearLayout)findViewById(R.id.Container2);
        Container[2] = (LinearLayout)findViewById(R.id.Container3);
        Container[3] = (LinearLayout)findViewById(R.id.Container4);
        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        getList();
    }
    public void Check(int i){
        check[i] = (check[i])?false:true;
        if(check[i]) Container[i].setBackgroundColor(Color.RED);
        else Container[i].setBackgroundColor(Color.WHITE);
    } // 이미지 선택 시 빨간 색으로 체크 표시
    public void ContainerAdjust(int size){
        switch (size){
            case 0:
                for(int i=0;i<4;i++) Container[i].setVisibility(View.GONE);
                break;
            case 1:
                for(int i=1;i<4;i++) Container[i].setVisibility(View.GONE);
                break;
            case 2:
                for(int i=2;i<4;i++) Container[i].setVisibility(View.GONE);
                break;
            case 3:
                Container[3].setVisibility(View.GONE);
                break;
            default:
                for(int i=0;i<4;i++) Container[i].setVisibility(View.VISIBLE);
        }
    } // 남은 이미지 수에 따라 UI 변동
    public void getList(){
        String url = Server.getBaseUrl()+"/label/start";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("writer", writer);
            reqObj.addProperty("title", title);
            Ion.with(ImgClassifyActivity.this)
                    .load(url)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(result.get("result").getAsInt() == 1) {
                                curObj = result.get("objects").getAsString();
                                tv.setText("다음 중 " + curObj + "은(는) 무엇인가요?");
                                curList = result.get("files").getAsJsonArray();
                                ContainerAdjust(curList.size());

                                for(int i=0;i<curList.size();i++) {
                                    String filename = curList.get(i).getAsString();
                                    Glide.with(ImgClassifyActivity.this).load(Server.getBaseUrl()+"/"+writer+"/"+title+"/temp/"+filename).into(ib[i]);
                                }
                            } else if (result.get("result").getAsInt() == 2) {
                                Toast.makeText(ImgClassifyActivity.this, "프로젝트의 모든 분류가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ImgClassifyActivity.this, ProjectListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // 서버로부터 분류할 이미지를 받아옴
    public void sendResult(){
        String url = Server.getBaseUrl()+"/label/end/1";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("writer", writer);
            reqObj.addProperty("title", title);
            reqObj.addProperty("objects", curObj);

            JsonArray sendList = new JsonArray();
            for(int i=0;i<curList.size();i++) {
                if(check[i]) sendList.add(curList.get(i).getAsString());
            }
            reqObj.add("name", sendList);

            Ion.with(ImgClassifyActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(result.get("result").getAsInt() == 1) {
                                for(int i=0;i<4;i++) {
                                    check[i] = false;
                                    Container[i].setBackgroundColor(Color.WHITE);
                                }
                                getList();
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    } // 분류 결과 전송
}
