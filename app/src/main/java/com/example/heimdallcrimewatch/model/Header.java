package com.example.heimdallcrimewatch.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.heimdallcrimewatch.R;

public class Header extends RelativeLayout {
    public static final String TAG = Header.class.getSimpleName();

    protected ImageView icon;
    private TextView label;

    public Header(Context context) {
        super(context);
    }

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Header(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initHeader() {
        inflateHeader();
    }

    private void inflateHeader() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.header, this);
        icon = (ImageView) findViewById(R.id.icon);
        label = (TextView) findViewById(R.id.title);
    }
}
