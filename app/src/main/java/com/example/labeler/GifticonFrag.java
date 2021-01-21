package com.example.labeler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GifticonFrag extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup containter, Bundle SavedInstanceState) {
        return inflater.inflate(R.layout.fragment_gifticon, containter, false);
    }
}
