package com.example.hometrainng.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.hometrainng.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StatuteActivity extends BaseActivity {
    @BindView(R.id.back_img)
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statute);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_img)
    public void onViewClicked() {
        finish();
    }
}
