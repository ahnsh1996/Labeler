package com.example.labeler;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ImgViewFragment extends Fragment {
    private ImageView iv;
    private String url;
    private Uri uri;
    private boolean isURL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_imgview, container, false);
        iv = rootview.findViewById(R.id.ImgView);

        isURL = getArguments().getBoolean("isURL");
        if(isURL)
            this.url = getArguments().getString("url");
        else
            this.uri = Uri.parse(getArguments().getString("url"));

        Glide.with(ImgViewFragment.this).load((isURL)?url:uri).into(iv);
        return rootview;
    }
    public static ImgViewFragment newInstance(boolean isURL, String url) {
        ImgViewFragment f = new ImgViewFragment();
        Bundle args = new Bundle();
        args.putBoolean("isURL", isURL);
        args.putString("url", url);
        f.setArguments(args);
        return f;
    }
}