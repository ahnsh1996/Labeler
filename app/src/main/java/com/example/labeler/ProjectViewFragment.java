package com.example.labeler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.skydoves.progressview.OnProgressChangeListener;
import com.skydoves.progressview.ProgressView;

public class ProjectViewFragment extends Fragment {
    private TextView titleText;
    private TextView descText;
    private ProgressView progressView;
    private TextView days;
    private Button selBtn;
    String title_text;
    String desc_text;
    String writer;
    String day;
    int progress;
    ProjectViewFragment(String title_text, String desc_text, String day, int progress, String writer) {
        this.title_text = title_text;
        this.desc_text = desc_text;
        this.day = day;
        this.progress = progress;
        this.writer = writer;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_projectlist, container, false);
        titleText = rootview.findViewById(R.id.titleText);
        descText = rootview.findViewById(R.id.descText);
        days = rootview.findViewById(R.id.days);
        progressView = rootview.findViewById(R.id.progress_view);
        selBtn = rootview.findViewById(R.id.selBtn);

        titleText.setText(title_text);
        descText.setText(desc_text);
        days.setText("마감일 : " + day);
        progressView.setProgress(progress);
        progressView.setLabelText(progress + "%");

        selBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
                intent.putExtra("writer", writer);
                intent.putExtra("title", title_text);
                startActivity(intent);
            }
        }); // 프로젝트 리스트에서 프로젝트 선택 시 프로젝트 상세 정보로

        return rootview;
    }
}