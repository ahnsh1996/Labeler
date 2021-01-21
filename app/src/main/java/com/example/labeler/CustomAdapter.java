package com.example.labeler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    ArrayList<CustomData> items = new ArrayList<CustomData>();
    Context context;
    String jwt;
    ProgressBar progressBar;

    public CustomAdapter(Context context, String jwt, ProgressBar progressBar) {
        this.context = context;
        this.jwt = jwt;
        this.progressBar = progressBar;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void addItem(CustomData item) {
        items.add(item);
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomItemView view = new CustomItemView(context);
        String text = items.get(position).project + ((items.get(position).active == 0)?" (마감)":" (진행)"); // 프로젝트의 마감 여부 표시
        view.setTextView(text);
        if(items.get(position).active == 0) // 마감된 경우 결과 다운로드 버튼 표시
            view.button.setVisibility(View.VISIBLE);

        view.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String saveFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Labeler";
                File dir = new File(saveFolderPath);
                if (!dir.exists())
                    dir.mkdir();

                String title = items.get(position).project;

                String url = Server.getBaseUrl()+"/project/download";
                final JsonObject reqObj = new JsonObject();
                try {
                    reqObj.addProperty("title", title);

                    Ion.with(context)
                            .load(url)
                            .addHeader("x-access-token", jwt)
                            .progressBar(progressBar)
                            .setJsonObjectBody(reqObj)
                            .write(new File(saveFolderPath+"/"+title+".zip"))
                            .setCallback(new FutureCallback<File>() {
                                @Override
                                public void onCompleted(Exception e, File file) {
                                    Toast.makeText(context, "파일의 다운로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }); // 의뢰한 프로젝트의 결과물 다운

        return view;
    }
}