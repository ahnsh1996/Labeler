package com.example.labeler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.JsonObject;
import com.pm10.library.CircleIndicator;

public class ProjectDetailFragment extends Fragment {
    private int type;
    private String topic;
    private String typeStr;
    private String content;
    private String url;
    private TextView topicText;
    private TextView contentText;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private JsonObject result;

    public ProjectDetailFragment(int type, String content, String url, JsonObject result) {
        this.type = type;
        this.content = content;
        this.url = url;
        this.result = result;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_detail, container, false);
        topicText = rootview.findViewById(R.id.topic);
        contentText = rootview.findViewById(R.id.contentText);
        viewPager = rootview.findViewById(R.id.pagerImg);
        circleIndicator = rootview.findViewById(R.id.circle_indicator);

        switch (type) {
            case 1:
                topic = "프로젝트 소개";
                typeStr = "contentimg";
                break;
            case 2:
                topic = "올바른 예시";
                typeStr = "correctimg";
                break;
            default:
                topic = "잘못된 예시";
                typeStr = "wrongimg";
        }
        topicText.setText(topic);
        contentText.setText(content);

        ProjectDetailAdapter adapter = new ProjectDetailAdapter(getChildFragmentManager());

        for(int i=0;i<result.size();i++)
            adapter.addItem(ImgViewFragment.newInstance(true, url + typeStr + "/" + result.get(Integer.toString(i)).getAsString()));

        viewPager.setAdapter(adapter);
        circleIndicator.setupWithViewPager(viewPager);

        return rootview;
    }
}