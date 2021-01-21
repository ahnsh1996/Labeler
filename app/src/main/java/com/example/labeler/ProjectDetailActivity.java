package com.example.labeler;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.lingala.zip4j.ZipFile;

import java.io.File;

public class ProjectDetailActivity extends AppCompatActivity {
    private TextView titleText;
    private ViewPager viewPager;
    private Button participateBtn;
    private final String PREFERENCE = "SECRET";
    private String jwt;
    private int type;
    private LinearLayout uploadLayout;
    private Button uploadBtn, submitBtn;
    private TextView uploadNum;
    private final int PICTURE_REQUEST_CODE = 99;
    private ClipData uploadData;
    private ProgressBar progressBar;
    private String writer, title;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle("프로젝트 상세보기");

        titleText = (TextView)findViewById(R.id.titleText);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        participateBtn = (Button)findViewById(R.id.button);
        uploadLayout = (LinearLayout)findViewById(R.id.uploadLayout);
        uploadBtn = (Button)findViewById(R.id.uploadBtn) ;
        uploadNum = (TextView)findViewById(R.id.uploadNum);
        submitBtn = (Button)findViewById(R.id.submit);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");

        Intent intent = getIntent();

        writer = intent.getExtras().getString("writer");
        title = intent.getExtras().getString("title");
        titleText.setText(title);

        setting(writer, title);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbum(PICTURE_REQUEST_CODE);
            }
        }); // 이미지 수집의 경우, 수집할 이미지 선택 버튼

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end();
            }
        }); // 이미지 수집의 경우, 이미지 선택 후 제출 버튼

        participateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (type) {
                    case 0: // 이미지 수집
                        uploadLayout.setVisibility(View.VISIBLE);
                        scrollView.invalidate();
                        scrollView.requestLayout();
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        break;
                    case 1: // 이미지 분류
                        intent.setClass(ProjectDetailActivity.this, ImgClassifyActivity.class);
                        break;
                    case 2: // 바운딩 박스
                        intent.setClass(ProjectDetailActivity.this, BoundingBoxActivity.class);
                        break;
                    default:
                        intent.setClass(ProjectDetailActivity.this, TextClassifyActivity.class);
                }
                if(type != 0) {
                    intent.putExtra("writer", writer);
                    intent.putExtra("title", title);
                    startActivity(intent);
                }
            }
        }); // 프로젝트 참가 버튼 클릭 시
    }

    private void getAlbum(int request_code) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request_code);
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        cursor.close();
        return path;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                uploadData = data.getClipData();
                uploadNum.setText(uploadData.getItemCount() + "개의 파일이 선택되었습니다.");
                uploadNum.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setting(String writer, String title) {
        String url = Server.getBaseUrl()+"/board/detail";
        String url2 = Server.getBaseUrl()+"/"+writer+"/"+title+"/";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("writer", writer);
            reqObj.addProperty("title", title);
            Ion.with(ProjectDetailActivity.this)
                    .load(url)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            ProjectDetailAdapter adapter = new ProjectDetailAdapter(getSupportFragmentManager());
                            adapter.addItem(new ProjectDetailFragment(1, result.get("db").getAsJsonObject().get("contenttext").getAsString(), url2, result.get("contentimg").getAsJsonObject()));
                            adapter.addItem(new ProjectDetailFragment(2, result.get("db").getAsJsonObject().get("correcttext").getAsString(), url2, result.get("correctimg").getAsJsonObject()));
                            adapter.addItem(new ProjectDetailFragment(3, result.get("db").getAsJsonObject().get("wrongtext").getAsString(), url2, result.get("wrongimg").getAsJsonObject()));
                            viewPager.setAdapter(adapter);
                            type = result.get("db").getAsJsonObject().get("type").getAsInt();
                        }
                    });
        } catch (Exception e){
            e.printStackTrace();
        }
    } // UI 등 세팅

    public void start() {
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("title", title);
            reqObj.addProperty("writer", writer);
            String url = Server.getBaseUrl()+"/label/start";
            Ion.with(ProjectDetailActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            progressBar.setProgress(0);
                            progressBar.setVisibility(View.GONE);
                            if(result.get("result").getAsInt() == 2) {
                                Toast.makeText(ProjectDetailActivity.this, "프로젝트의 목표 수집량에 도달하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ProjectDetailActivity.this, ProjectListActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // 수집을 위한 프로젝트 정보를 받아옴
    public void end() {
        try {
            ZipFile dataZip = new ZipFile(ProjectDetailActivity.this.getFilesDir().toString()+"/labeling.zip");
            for(int i=0;i<uploadData.getItemCount();i++)
                dataZip.addFile(getRealPathFromURI(uploadData.getItemAt(i).getUri()));

            progressBar.setVisibility(View.VISIBLE);
            Ion.getDefault(ProjectDetailActivity.this).getConscryptMiddleware().enable(false);
            String url = Server.getBaseUrl()+"/label/end/0";
            Ion.with(ProjectDetailActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .uploadProgressBar(progressBar)
                    .setMultipartParameter("title", title)
                    .setMultipartParameter("writer", writer)
                    .setMultipartParameter("filenum", Integer.toString(uploadData.getItemCount()))
                    .setMultipartFile("label", "application/zip", dataZip.getFile())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if(result.get("result").getAsInt() == 1) {
                                dataZip.getFile().delete();
                                Toast.makeText(ProjectDetailActivity.this, "정상적으로 파일이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                                start(); // 업로드 이후 업데이트 된 프로젝트 정보 갱신(프로젝트 만료 등)
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // 이미지 수집의 경우, 서버로 파일 업로드
}