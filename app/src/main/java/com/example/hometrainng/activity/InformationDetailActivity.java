package com.example.hometrainng.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.adapter.InformationAdapter;
import com.example.hometrainng.adapter.NoticeAdapter;
import com.example.hometrainng.db.Notice;
import com.example.hometrainng.entity.InformationDetailEntity;
import com.example.hometrainng.entity.InformationNoticeEntity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InformationDetailActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.information_detail_recycleView)
    RecyclerView informationDetailRecycleView;
    @BindView(R.id.information_img)
    ImageView informationImg;
    @BindView(R.id.information_text)
    TextView informationText;
    private Context mContext;
    private List<InformationNoticeEntity> informationNoticeEntityList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        init();

    }

    public void init() {
        informationImg.setBackgroundResource(R.drawable.round_imageview);
        informationText.setText(R.string.information_detail_title);
        informationNoticeEntityList=new ArrayList<>();
        initData();
        initRecycleview();
    }

    private void initRecycleview() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        informationDetailRecycleView.setLayoutManager(layoutManager);
         NoticeAdapter adapter = new NoticeAdapter(mContext, informationNoticeEntityList);
        informationDetailRecycleView.setAdapter(adapter);

    }

    private void initData() {
        List<Notice> noticeList=LitePal.select("*").order("createTime desc").find(Notice.class);
        for (int i=0;i<noticeList.size();i++){
            InformationNoticeEntity informationNoticeEntity=new InformationNoticeEntity();
            informationNoticeEntity.setData(noticeList.get(i).getCreateTime().substring(0,10));
            informationNoticeEntity.setTitle(noticeList.get(i).getTitle());
            informationNoticeEntity.setMsg(noticeList.get(i).getContent());
           informationNoticeEntity.setUrl(noticeList.get(i).getUrl());
            informationNoticeEntityList.add(informationNoticeEntity);
        }
    }


    @OnClick(R.id.back_img)
    public void onViewClicked() {
        finish();
    }
}
