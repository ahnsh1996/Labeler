package com.example.labeler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainActivity extends AppCompatActivity {
    private LinearLayout screen;
    private Button login_btn;
    private Button register_btn;
    private EditText id_edit;
    private EditText pw_edit;
    private final String PREFERENCE = "SECRET";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("로그인");

        screen = (LinearLayout)findViewById(R.id.screen);
        login_btn = (Button)findViewById(R.id.button_login);
        register_btn = (Button)findViewById(R.id.button_register);
        id_edit = (EditText)findViewById(R.id.editText_id);
        pw_edit = (EditText)findViewById(R.id.editText_pw);

        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus() instanceof EditText) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        }); // 입력 중 키보드 제외 화면(배경) 선택 시 키보드 숨김
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        }); // 회원가입 버튼 클릭 시
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = id_edit.getText().toString();
                String pw = pw_edit.getText().toString();

                String url = Server.getBaseUrl()+"/user/login";
                final JsonObject reqObj = new JsonObject();
                try {
                    reqObj.addProperty("accountid", id);
                    reqObj.addProperty("accountpw", pw);

                    Ion.with(MainActivity.this)
                            .load(url)
                            .setJsonObjectBody(reqObj)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if (result.get("result").getAsInt() == 1){ // 로그인 성공 시
                                        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("jwt", result.get("jwt").getAsString());
                                        editor.putString("accountID", id);
                                        editor.commit();
                                        // jwt를 SharedPreferences에 저장
                                        Toast.makeText(MainActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainActivity.this, ProjectListActivity.class);
                                        startActivity(intent);
                                    }
                                    else { // 로그인 실패 시
                                        Toast.makeText(MainActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }); // 로그인 버튼 클릭 시
    }
}
