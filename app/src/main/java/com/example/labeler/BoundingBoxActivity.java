package com.example.labeler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Random;

public class BoundingBoxActivity extends AppCompatActivity {
    private Bitmap editBitmap;
    private Bitmap init;
    private Canvas canvas;
    private Paint paint;
    private int CurPosition;
    private Button drawBtn, cancelBtn;
    private Spinner objSelector;
    private ArrayList<BoundingBox> boxList;
    private CropImageView ImageView;
    private ArrayList<String> objList;
    private boolean yolo;
    private ArrayList<Integer> color;
    private String writer, title, filename;
    private StringBuffer sendData;
    private final String PREFERENCE = "SECRET";
    private String jwt;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boundingbox);
        setTitle("이미지 라벨링");

        cancelBtn = (Button)findViewById(R.id.btn_cancel);
        drawBtn = (Button)findViewById(R.id.btn_draw);
        objSelector = (Spinner)findViewById(R.id.ObjSeletor);
        boxList = new ArrayList<BoundingBox>();
        canvas = new Canvas();
        paint = new Paint();
        objList = new ArrayList<String>();
        ImageView = (CropImageView)findViewById(R.id.ImageView);
        CurPosition = 0;
        color = new ArrayList<Integer>();
        color.add(Color.BLUE);
        sendData = new StringBuffer();

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        Intent intent = getIntent();

        writer = intent.getExtras().getString("writer");
        title = intent.getExtras().getString("title");

        int strokeWidth = BoundingBoxActivity.this.getResources().getDimensionPixelSize(R.dimen.my_stroke_width);
        int textSize = BoundingBoxActivity.this.getResources().getDimensionPixelSize(R.dimen.my_text_size);
        paint.setStrokeWidth(strokeWidth);
        paint.setTextSize(textSize);

        getImg();

        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float temp[] = ImageView.getCropPoints();
                boxList.add(new BoundingBox(CurPosition, temp[0], temp[1], temp[4], temp[5], yolo, editBitmap.getWidth(), editBitmap.getHeight()));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(CurPosition+" "+objList.get(CurPosition), temp[0]+9, temp[1]+30, paint);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(temp[0], temp[1], temp[4], temp[5], paint);
                ImageView.setImageBitmap(editBitmap);
            }
        }); // 영역 선택 후 바운딩 박스 그리기 버튼 클릭 시

        objSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CurPosition = position;
                paint.setColor(color.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        }); // 객체 선택
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                editBitmap = init.copy(Bitmap.Config.ARGB_8888,true);
                ImageView.setImageBitmap(editBitmap);
                canvas.setBitmap(editBitmap);
                boxList.clear();
            }
        }); // 바운딩 취소 버튼 클릭 시
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_btn:
                if(!boxList.isEmpty()) sendData.append(boxList.get(0).toString());
                for(int i=1;i<boxList.size();i++)
                    sendData.append("\n"+boxList.get(i).toString());
                send();
                objList.clear();
                boxList.clear();
                sendData.delete(0, sendData.length());
                getImg();
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    } // 확인 버튼 클릭 시
    public void getImg() {
        String url = Server.getBaseUrl()+"/label/start";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("title", title);
            reqObj.addProperty("writer", writer);
            Ion.with(BoundingBoxActivity.this)
                    .load(url)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(result.get("result").getAsInt() == 1) {
                                if (result.get("format").getAsBoolean()) yolo = true;
                                filename = result.get("files").getAsJsonArray().get(0).getAsString();
                                String url2 = Server.getBaseUrl()+"/" + writer + "/" + title + "/temp/" + filename;
                                if (objList.isEmpty()) {
                                    JsonArray temp = result.get("objects").getAsJsonArray();
                                    color.clear();
                                    Random random = new Random();
                                    for (int i = 0; i < temp.size(); i++) {
                                        objList.add(temp.get(i).getAsString());
                                        color.add(Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                                    }
                                    ArrayAdapter arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, objList);
                                    objSelector.setAdapter(arrayAdapter);
                                }
                                Glide.with(BoundingBoxActivity.this)
                                        .asBitmap()
                                        .load(url2)
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                init = resource.copy(Bitmap.Config.ARGB_8888, true);
                                                editBitmap = init.copy(Bitmap.Config.ARGB_8888, true);
                                                canvas.setBitmap(editBitmap);
                                                ImageView.setImageBitmap(editBitmap);
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });
                            } else if (result.get("result").getAsInt() == 2){
                                Toast.makeText(BoundingBoxActivity.this, "프로젝트의 라벨링이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BoundingBoxActivity.this, ProjectListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // 라벨링을 위한 정보를 받아옴
    public void send() {
        String url = Server.getBaseUrl()+"/label/end/2";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("title", title);
            reqObj.addProperty("writer", writer);
            reqObj.addProperty("name", filename);
            reqObj.addProperty("content", sendData.toString());
            Ion.with(BoundingBoxActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // 라벨링 결과 전송
}