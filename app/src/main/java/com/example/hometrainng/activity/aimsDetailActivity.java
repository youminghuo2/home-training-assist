package com.example.hometrainng.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hometrainng.R;
import com.example.hometrainng.adapter.GoalsAdapter;
import com.example.hometrainng.adapter.InformationAdapter;
import com.example.hometrainng.db.Goals;
import com.example.hometrainng.db.Issues;
import com.example.hometrainng.entity.GoalsEntity;
import com.example.hometrainng.entity.InformationDetailEntity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class aimsDetailActivity extends BaseActivity {
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.information_img)
    ImageView informationImg;
    @BindView(R.id.information_text)
    TextView informationText;
    @BindView(R.id.information_detail_recycleView)
    RecyclerView informationDetailRecycleView;
    @BindView(R.id.image_text)
    TextView imageText;
    private Context mContext;
    private List<GoalsEntity> goalsEntityList;
    private String aims_subject_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aims_detail);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        aims_subject_type = intent.getStringExtra("aims_subject_type");
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 测试数据方法
         */
        goalsEntityList = new ArrayList<>();
        initData();
        initRecycleview();

    }

    public void init() {
        if (aims_subject_type.equals("aims")) {
            informationImg.setImageResource(R.mipmap.aims_big_icon);
            informationText.setText(getString(R.string.goal));
        } else {
            informationImg.setImageResource(R.mipmap.subject_big_icon);
            informationText.setText(getString(R.string.topic));
        }

    }


    private void initRecycleview() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        informationDetailRecycleView.setLayoutManager(layoutManager);
        GoalsAdapter adapter = new GoalsAdapter(mContext, goalsEntityList);
        informationDetailRecycleView.setAdapter(adapter);

    }

    private void initData() {
        if (aims_subject_type.equals("aims")) {
            List<Goals> goalsList = LitePal.select("createTime", "goal").order("createTime desc").find(Goals.class);
            for (int i = 0; i < goalsList.size(); i++) {
                GoalsEntity goalsEntity = new GoalsEntity();
                goalsEntity.setData(goalsList.get(i).getCreateTime().substring(0, 10));
                goalsEntity.setMsg(goalsList.get(i).getGoal());
                goalsEntityList.add(goalsEntity);
            }
        } else {
            List<Issues> issuesList = LitePal.select("createtime", "issue").order("createTime desc").find(Issues.class);
            for (int i = 0; i < issuesList.size(); i++) {
                GoalsEntity goalsEntity = new GoalsEntity();
                goalsEntity.setData(issuesList.get(i).getCreateTime().substring(0, 10));
                goalsEntity.setMsg(issuesList.get(i).getIssue());
                goalsEntityList.add(goalsEntity);
            }
        }


    }


    @OnClick(R.id.back_img)
    public void onViewClicked() {
        finish();
    }
}

