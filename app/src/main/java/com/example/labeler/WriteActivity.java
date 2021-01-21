package com.example.labeler;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.pm10.library.CircleIndicator;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WriteActivity extends AppCompatActivity {
    final int DISC_PICTURE_REQUEST_CODE = 100;
    final int CORR_PICTURE_REQUEST_CODE = 101;
    final int INCORR_PICTURE_REQUEST_CODE = 102;
    final int UPLOAD_PICTURE_REQUEST_CODE = 103;
    private ViewPager descriptionPager;
    private ViewPager correctPager;
    private ViewPager incorrectPager;
    private EditText titleText;
    private EditText descriptionText;
    private EditText correctText;
    private EditText incorrectText;
    private EditText daysText;
    private ProjectDetailAdapter descriptionAdapter;
    private ProjectDetailAdapter correctAdapter;
    private ProjectDetailAdapter incorrctAdapter;
    private Button descBtn;
    private Button corrBtn;
    private Button incorrBtn;
    private Button submitBtn;
    private ClipData descImg;
    private ClipData corrImg;
    private ClipData incorrImg;
    private ClipData uploadImg;
    private NormalFile uploadCSV;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    private final String PREFERENCE = "SECRET";
    private String jwt;
    private String writer;
    private LinearLayout uploadLayout, numSelLayout;
    private RadioGroup radioGroup, radioGroup2, boxType;
    private Button uploadBtn;
    private TextView uploadNum;
    private int type;
    private ArrayList<String> objList;
    private Button objAdd, objUpdate, objDel;
    private LinearLayout objLayout, boxTypeLayout;
    private EditText objInput;
    private ListView objView;
    private ArrayAdapter objAdapter;
    private boolean Yolo;
    private EditText numSel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        setTitle("프로젝트 업로드");

        descBtn = (Button) findViewById(R.id.descBtn);
        corrBtn = (Button) findViewById(R.id.corrBtn);
        incorrBtn = (Button) findViewById(R.id.incorrBtn);
        submitBtn = (Button)findViewById(R.id.submitBtn);
        descriptionPager = (ViewPager) findViewById(R.id.descriptionImg);
        correctPager = (ViewPager) findViewById(R.id.correctImg);
        incorrectPager = (ViewPager)findViewById(R.id.incorrectImg);
        titleText = (EditText)findViewById(R.id.titleText);
        descriptionText = (EditText)findViewById(R.id.descriptionText);
        correctText = (EditText)findViewById(R.id.correcText);
        incorrectText = (EditText)findViewById(R.id.incorrecText);
        daysText = (EditText)findViewById(R.id.daysText);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottomNavi);
        uploadLayout = (LinearLayout)findViewById(R.id.uploadLayout);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup2 = (RadioGroup)findViewById(R.id.radioGroup2);
        numSelLayout = (LinearLayout)findViewById(R.id.numSelLayout);
        uploadBtn = (Button)findViewById(R.id.uploadBtn);
        uploadNum = (TextView)findViewById(R.id.uploadNum);
        objList = new ArrayList<String>();
        objAdd = (Button)findViewById(R.id.objAddBtn);
        objUpdate = (Button)findViewById(R.id.objUpdateBtn);
        objDel = (Button)findViewById(R.id.objDelBtn);
        objLayout = (LinearLayout)findViewById(R.id.objListLayout);
        objInput = (EditText)findViewById(R.id.objEdit);
        objView = (ListView)findViewById(R.id.objList);
        objAdapter = new ArrayAdapter(WriteActivity.this, android.R.layout.simple_list_item_single_choice, objList);
        objView.setAdapter(objAdapter);
        boxType = (RadioGroup)findViewById(R.id.boxType);
        boxTypeLayout = (LinearLayout)findViewById(R.id.boxTypeLayout);
        numSel = (EditText)findViewById(R.id.numSel);

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        jwt = pref.getString("jwt", "");
        writer =  pref.getString("accountID", "");

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        bottomNavigationView.setSelectedItemId(R.id.require);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch ((item.getItemId())) {
                    case R.id.home:
                        intent = new Intent(WriteActivity.this, ProjectListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.shop:
                        intent = new Intent(WriteActivity.this, ShopActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.mypage:
                        intent = new Intent(WriteActivity.this, MyPageActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });
        objAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(objInput.getText().toString().equals("")) {
                    Toast.makeText(WriteActivity.this, "분류할 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                objList.add(objInput.getText().toString());
                objInput.setText("");
                objView.clearChoices();
                objAdapter.notifyDataSetChanged();
                ListViewResize(objAdapter, objView);
            }
        }); // 분류할 객체 추가
        objUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(objInput.getText().toString().equals("")) {
                    Toast.makeText(WriteActivity.this, "분류할 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (objList.size() > 0) {
                    int checked = objView.getCheckedItemPosition();
                    if (checked > -1 && checked < objList.size()) {
                        objList.set(checked, objInput.getText().toString());
                        objInput.setText("");
                        objView.clearChoices();
                        objAdapter.notifyDataSetChanged();
                    }
                    ListViewResize(objAdapter, objView);
                }
            }
        }); // 분류할 객체 정보 수정
        objDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (objList.size() > 0) {
                    int checked = objView.getCheckedItemPosition();
                    if (checked > -1 && checked < objList.size()) {
                        objList.remove(checked);
                        objView.clearChoices();
                        objAdapter.notifyDataSetChanged();
                    }
                    ListViewResize(objAdapter, objView);
                }
            }
        }); // 분류할 객체 정보 삭제

        descBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbum(DISC_PICTURE_REQUEST_CODE);
            }
        }); // 설명을 위한 사진 업로드 버튼
        corrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbum(CORR_PICTURE_REQUEST_CODE);
            }
        }); // 올바른 예시 설명을 위한 사진 업로드 버튼
        incorrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAlbum(INCORR_PICTURE_REQUEST_CODE);
            }
        }); // 잘못된 예시 설명을 위한 사진 업로드 버튼
        boxType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Yolo = (checkedId==R.id.notYolo)?false:true;
            }
        }); // 바운딩 박스 라벨링의 경우 Yolo인지 여부
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(type > 1) radioGroup2.clearCheck();
                switch (checkedId) {
                    case R.id.ImgCollectRadio:
                        numSelLayout.setVisibility(View.VISIBLE);
                        uploadLayout.setVisibility(View.GONE);
                        objLayout.setVisibility(View.GONE);
                        boxTypeLayout.setVisibility(View.GONE);
                        type = 0;
                        break;
                    default: // 이미지 분류
                        numSelLayout.setVisibility(View.GONE);
                        uploadLayout.setVisibility(View.VISIBLE);
                        objLayout.setVisibility(View.VISIBLE);
                        boxTypeLayout.setVisibility(View.GONE);
                        type = 1;
                }
            }
        }); // 라벨링 종류에 따라 추가적인 입력창 표시
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(type < 2) radioGroup.clearCheck();
                switch (checkedId) {
                    case R.id.ImgBoxRadio:
                        numSelLayout.setVisibility(View.GONE);
                        uploadLayout.setVisibility(View.VISIBLE);
                        objLayout.setVisibility(View.VISIBLE);
                        boxTypeLayout.setVisibility(View.VISIBLE);
                        type = 2;
                        break;
                    default: // 텍스트 분류
                        numSelLayout.setVisibility(View.GONE);
                        uploadLayout.setVisibility(View.VISIBLE);
                        objLayout.setVisibility(View.VISIBLE);
                        boxTypeLayout.setVisibility(View.GONE);
                        type = 3;
                }
            }
        }); // 라벨링 종류에 따라 추가적인 입력창 표시
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type < 3)
                    getAlbum(UPLOAD_PICTURE_REQUEST_CODE);
                else
                    getCSV(Constant.REQUEST_CODE_PICK_FILE);
            }
        }); // 라벨링할 대상 파일 업로드
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        }); // 제출 버튼 클릭 시
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

    private void getAlbum(int request_code) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), request_code);
    }

    private void getCSV(int request_code) {
        Intent intent = new Intent(this, NormalFilePickActivity.class);
        intent.putExtra(Constant.MAX_NUMBER, 1);
        intent.putExtra(NormalFilePickActivity.SUFFIX, new String[] {"csv"});
        startActivityForResult(intent, request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DISC_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                descImg = data.getClipData();

                if (clipData != null) {
                    descriptionPager.setVisibility(View.VISIBLE);
                    descriptionAdapter = new ProjectDetailAdapter(getSupportFragmentManager());

                    for (int i = 0; i < clipData.getItemCount(); i++)
                        descriptionAdapter.addItem(ImgViewFragment.newInstance(false, clipData.getItemAt(i).getUri().toString()));

                    descriptionPager.setAdapter(descriptionAdapter);
                    CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
                    circleIndicator.setupWithViewPager(descriptionPager);
                    circleIndicator.setVisibility(View.VISIBLE);
                }
            }
        } else if (requestCode == CORR_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                corrImg = data.getClipData();

                if (clipData != null) {
                    correctPager.setVisibility(View.VISIBLE);
                    correctAdapter = new ProjectDetailAdapter(getSupportFragmentManager());

                    for (int i = 0; i < clipData.getItemCount(); i++)
                        correctAdapter.addItem(ImgViewFragment.newInstance(false, clipData.getItemAt(i).getUri().toString()));

                    correctPager.setAdapter(correctAdapter);
                    CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator2);
                    circleIndicator.setupWithViewPager(correctPager);
                    circleIndicator.setVisibility(View.VISIBLE);
                }
            }
        } else if (requestCode == INCORR_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                incorrImg = data.getClipData();

                if (clipData != null) {
                    incorrectPager.setVisibility(View.VISIBLE);
                    incorrctAdapter = new ProjectDetailAdapter(getSupportFragmentManager());

                    for (int i = 0; i < clipData.getItemCount(); i++)
                        incorrctAdapter.addItem(ImgViewFragment.newInstance(false, clipData.getItemAt(i).getUri().toString()));

                    incorrectPager.setAdapter(incorrctAdapter);
                    CircleIndicator circleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator3);
                    circleIndicator.setupWithViewPager(incorrectPager);
                    circleIndicator.setVisibility(View.VISIBLE);
                }
            }
        }
        else if (requestCode == UPLOAD_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                uploadImg = data.getClipData();
                uploadNum.setText(uploadImg.getItemCount() + "개의 파일이 선택되었습니다.");
            }
        }
        else {
            if (resultCode == RESULT_OK) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                uploadCSV = list.get(0);
            }
        }
    }
    public void send() {
        sendImg();
    }
    public void sendImg(){
        try {
            ZipFile descZip = new ZipFile(WriteActivity.this.getFilesDir().toString()+"/contentimg.zip");
            ZipFile corrZip = new ZipFile(WriteActivity.this.getFilesDir().toString()+"/correctimg.zip");
            ZipFile incorrZip = new ZipFile(WriteActivity.this.getFilesDir().toString()+"/wrongimg.zip");
            ZipFile dataZip = new ZipFile(WriteActivity.this.getFilesDir().toString()+"/labeling.zip");
            for(int i=0;i<descImg.getItemCount();i++)
                descZip.addFile(getRealPathFromURI(descImg.getItemAt(i).getUri()));
            for(int i=0;i<corrImg.getItemCount();i++)
                corrZip.addFile(getRealPathFromURI(corrImg.getItemAt(i).getUri()));
            for(int i=0;i<incorrImg.getItemCount();i++)
                incorrZip.addFile(getRealPathFromURI(incorrImg.getItemAt(i).getUri()));
            if(type > 0 && type < 3) {
                for (int i = 0; i < uploadImg.getItemCount(); i++)
                    dataZip.addFile(getRealPathFromURI(uploadImg.getItemAt(i).getUri()));
            } else if(type == 3) {
                dataZip.addFile(uploadCSV.getPath());
                dataZip.renameFile(uploadCSV.getPath().substring(uploadCSV.getPath().lastIndexOf("/")+1), "data.csv");
            }

            ZipFile totalZip = new ZipFile(WriteActivity.this.getFilesDir().toString()+"/data_"+writer+"_"+titleText.getText().toString()+".zip");
            if(type == 0) totalZip.addFiles(Arrays.asList(descZip.getFile(), corrZip.getFile(), incorrZip.getFile()));
            else totalZip.addFiles(Arrays.asList(descZip.getFile(), corrZip.getFile(), incorrZip.getFile(), dataZip.getFile()));

            progressBar.setVisibility(View.VISIBLE);
            Ion.getDefault(WriteActivity.this).getConscryptMiddleware().enable(false);
            String url = Server.getBaseUrl()+"/project/upload";
            Ion.with(WriteActivity.this)
                    .load(url)
                    .uploadProgressBar(progressBar)
                    .setMultipartFile("project", "application/zip", totalZip.getFile())
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            descZip.getFile().delete();
                            corrZip.getFile().delete();
                            incorrZip.getFile().delete();
                            dataZip.getFile().delete();
                            totalZip.getFile().delete();
                            sendContent();
                        }
                    });
        } catch (ZipException e) {
            e.printStackTrace();
        }
    } // 라벨링 대상 파일 전송
    public void sendContent(){
        String title = titleText.getText().toString();
        String content_text = descriptionText.getText().toString();
        String correct_text = correctText.getText().toString();
        String wrong_text = incorrectText.getText().toString();
        String days = daysText.getText().toString();

        String url = Server.getBaseUrl()+"/project/save";
        final JsonObject reqObj = new JsonObject();
        try {
            reqObj.addProperty("title", title);
            reqObj.addProperty("type", Integer.toString(type));
            reqObj.addProperty("point", 10);
            reqObj.addProperty("contenttext", content_text);
            reqObj.addProperty("correcttext", correct_text);
            reqObj.addProperty("wrongtext", wrong_text);
            reqObj.addProperty("closingdate", days);

            if(type == 2) reqObj.addProperty("format", Yolo);

            if(type > 0) {
                JsonArray list = new JsonArray();
                for (int i=0;i<objList.size();i++)
                    list.add(objList.get(i));
                reqObj.add("objects", list);
            } else {
                reqObj.addProperty("filenum", Integer.parseInt(numSel.getText().toString()));
            }

            Ion.with(WriteActivity.this)
                    .load(url)
                    .setHeader("x-access-token", jwt)
                    .setJsonObjectBody(reqObj)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result.get("result").getAsInt() != 1){
                                Toast.makeText(WriteActivity.this, result.get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(WriteActivity.this, ProjectListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // 프로젝트 정보 전송
    public void ListViewResize(ArrayAdapter mListViewAdapter, ListView mListView) {
        int totalHeight = mListViewAdapter.getCount() * (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 45, this.getResources().getDisplayMetrics());
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = totalHeight;
        mListView.setLayoutParams(params);
    }
}