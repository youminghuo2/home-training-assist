package com.example.hometrainng.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hometrainng.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends BaseActivity {
    @BindView(R.id.homeTv)
    TextView homeTv;
    @BindView(R.id.back_img)
    ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.homeTv)
    public void onViewClicked() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(getString(R.string.url)));
        startActivity(i);
    }

    @OnClick(R.id.back_img)
    public void onBackClicked() {
        finish();
    }
}
