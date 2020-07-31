package com.example.hometrainng.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.hometrainng.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AttentionActivity extends BaseActivity {
    @BindView(R.id.back_img)
    ImageView backImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back_img)
    public void onViewClicked() {
        finish();
    }


}
