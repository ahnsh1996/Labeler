package com.example.labeler;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private boolean id_check;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener myDatePicker;
    private LinearLayout screen;
    private Button idCheck_btn;
    private Button reg_btn;
    private EditText id_edit;
    private EditText pw_edit;
    private EditText pw2_edit;
    private EditText name_edit;
    private EditText birth_edit;
    private EditText phone_edit;
    private ImageView id_checkImg;
    private ImageView pw_checkImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialization();

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
        idCheck_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = id_edit.getText().toString();

                String url = Server.getBaseUrl()+"/user/new/check";
                final JsonObject reqObj = new JsonObject();
                try {
                    reqObj.addProperty("accountid", id);

                    Ion.with(RegisterActivity.this)
                            .load(url)
                            .setJsonObjectBody(reqObj)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if (result.get("result").getAsInt() == 1){ // id 중복 체크 성공 시
                                        Toast.makeText(RegisterActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                        id_check = true;
                                        id_checkImg.setVisibility(View.VISIBLE);
                                    }
                                    else { // 실패 시
                                        Toast.makeText(RegisterActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                        id_check = false;
                                        id_checkImg.setVisibility(View.GONE);
                                    }
                                }
                            });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        pw_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String pw = pw_edit.getText().toString();
                String pw2 = pw2_edit.getText().toString();
                if (!pw.equals("") && pw.equals(pw2))
                    pw_checkImg.setVisibility(View.VISIBLE);
                else
                    pw_checkImg.setVisibility(View.GONE);
            }
        }); // 패스워드와 패스워드 확인의 입력이 같은지
        pw2_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String pw = pw_edit.getText().toString();
                String pw2 = pw2_edit.getText().toString();
                if (!pw.equals("") && pw.equals(pw2))
                    pw_checkImg.setVisibility(View.VISIBLE);
                else
                    pw_checkImg.setVisibility(View.GONE);
            }
        });
        birth_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, myDatePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                //datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
            }
        }); // 생일 입력
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = id_edit.getText().toString();
                String pw = pw_edit.getText().toString();
                String pw2 = pw2_edit.getText().toString();
                String name = name_edit.getText().toString();
                String birth = birth_edit.getText().toString();
                String phone = phone_edit.getText().toString();

                if (!pw.equals(pw2)){ // 패스워드 미일치 시
                    Toast.makeText(RegisterActivity.this, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!id_check) { // id 중복 검사가 확인되지 않았을 경우
                    Toast.makeText(RegisterActivity.this, "중복이 확인되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = Server.getBaseUrl()+"/user/new";
                final JsonObject reqObj = new JsonObject();
                try {
                    reqObj.addProperty("accountid", id);
                    reqObj.addProperty("accountpw", pw);
                    reqObj.addProperty("name", name);
                    reqObj.addProperty("birth", birth);
                    reqObj.addProperty("phonenumber", phone);

                    Ion.with(RegisterActivity.this)
                            .load(url)
                            .setJsonObjectBody(reqObj)
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    if (result.get("result").getAsInt() == 1){ // 회원가입 성공 시
                                        Toast.makeText(RegisterActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                        id_check = true;
                                        finish();
                                    }
                                    else { // 실패 시
                                        Toast.makeText(RegisterActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                        id_check = false;
                                    }
                                }
                            });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }); // 회원가입 버튼 클릭 시
    }

    private void initialization() {
        setTitle("회원가입");
        screen = (LinearLayout)findViewById(R.id.screen);
        idCheck_btn = (Button)findViewById(R.id.button_idCheck);
        reg_btn = (Button)findViewById(R.id.button_register);
        id_edit = (EditText)findViewById(R.id.editText_id);
        pw_edit = (EditText)findViewById(R.id.editText_pw);
        pw2_edit = (EditText)findViewById(R.id.editText_pw2);
        name_edit = (EditText)findViewById(R.id.editText_name);
        birth_edit = (EditText)findViewById(R.id.editText_birth);
        phone_edit = (EditText)findViewById(R.id.editText_phone);
        id_checkImg = (ImageView)findViewById(R.id.id_check);
        pw_checkImg = (ImageView)findViewById(R.id.pw_check);

        myCalendar = Calendar.getInstance();
        myDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        birth_edit.setText(sdf.format(myCalendar.getTime()));
    }
}
