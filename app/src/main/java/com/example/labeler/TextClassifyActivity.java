package com.example.labeler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class TextClassifyActivity extends AppCompatActivity {
    private TextView content;
    private Spinner selector;
    private Button button;
    private String writer;
    private String title;
    private String jwt;
    private final String PREFERENCE = "SECRET";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt_classify);
        setTitle("텍스트 분류");

        content = (TextView)findViewById(R.id.content);
        selector = (Spinner)findViewById(R.id.Selector);
        button = (Button)findViewById(R.id.button);

        Intent intent = getIntent();

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        writer = intent.getExtras().getString("writer");
        title = intent.getExtras().getString("title");

        getData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInfo();
            }
        });
    }
    public void getData() {
        String url = Server.getBaseUrl()+"/label/start";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("title", title);
            reqObj.addProperty("writer", writer);
            Ion.with(TextClassifyActivity.this)
                    .load(url)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(result.get("result").getAsInt() == 1) {
                                content.setText(result.get("text").getAsString());
                                JsonArray temp = result.getAsJsonArray("objects");
                                ArrayList<String> spinnerList = new ArrayList<String>();
                                for (int i = 0; i < temp.size(); i++)
                                    spinnerList.add(temp.get(i).getAsString());
                                ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerList);
                                selector.setAdapter(arrayAdapter);
                            } else if(result.get("result").getAsInt() == 2) {
                                Toast.makeText(TextClassifyActivity.this, "프로젝트의 모든 분류가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TextClassifyActivity.this, ProjectListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // 분류를 위한 정보를 받아옴
    public void sendInfo() {
        String url = Server.getBaseUrl()+"/label/end/3";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("title", title);
            reqObj.addProperty("writer", writer);
            reqObj.addProperty("objects", selector.getSelectedItem().toString());
            reqObj.addProperty("text", content.getText().toString());
            Ion.with(TextClassifyActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(result.get("result").getAsInt() == 1)
                                getData();
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // 분류 결과를 전송
}
