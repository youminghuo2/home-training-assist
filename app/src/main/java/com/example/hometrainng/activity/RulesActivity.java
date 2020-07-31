package com.example.hometrainng.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import com.example.hometrainng.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RulesActivity extends BaseActivity {

    @BindView(R.id.rules_button)
    Button rulesButton;
    @BindView(R.id.rules_text)
    TextView rulesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        ButterKnife.bind(this);
        rulesTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @OnClick(R.id.rules_button)
    public void onViewClicked() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
