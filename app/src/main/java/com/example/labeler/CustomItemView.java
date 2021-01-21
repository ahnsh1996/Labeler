package com.example.labeler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CustomItemView extends LinearLayout {
    TextView textView;
    Button button;
    public CustomItemView(Context context) {
        super(context);

        init(context);
    }

    public CustomItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_btn_item, this, true);

        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.button);
    }

    public void setTextView(String title) {
        textView.setText(title);
    }
}
