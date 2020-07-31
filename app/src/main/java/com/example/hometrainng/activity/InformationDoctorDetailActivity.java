package com.example.hometrainng.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.hometrainng.R;
import com.example.hometrainng.adapter.InformationAdapter;
import com.example.hometrainng.db.Messages;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.entity.InformationDetailEntity;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxSPTool;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InformationDoctorDetailActivity extends BaseActivity {

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
    private List<InformationDetailEntity> informationDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_doctor_detail);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        init();
    }

    public void init() {
        Therapist therapist = LitePal.where("flag=?", "responsible").findFirst(Therapist.class);
        String avatarPath = "";
        if (therapist == null) {
            Glide.with(mContext).load(R.mipmap.msg_info_detail).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(informationImg);
        } else {
            avatarPath= Utils.getPhotoUrl(therapist.getPhotoPath(), RxSPTool.getString(mContext,"token"));
            imageText.setText(therapist.getLastName() + therapist.getFirstName());
            imageText.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(avatarPath).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(informationImg);
        }
        informationText.setText(R.string.infromation_doctor_detail_title);
        informationDetailList = new ArrayList<>();
        initData();
        initRecycleview();
    }

    private void initRecycleview() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        informationDetailRecycleView.setLayoutManager(layoutManager);
        InformationAdapter adapter = new InformationAdapter(mContext, informationDetailList);
        informationDetailRecycleView.setAdapter(adapter);

    }

    private void initData() {
        List<Messages> messagesList = LitePal.select("*").order("createtime desc").find(Messages.class);
        for (int i = 0; i < messagesList.size(); i++) {
            InformationDetailEntity informationDetailEntity = new InformationDetailEntity();
            informationDetailEntity.setData(messagesList.get(i).getCreateTime().substring(0, 10));
            informationDetailEntity.setTitle(messagesList.get(i).getTitle());
            informationDetailEntity.setMsg(messagesList.get(i).getContent());
            informationDetailList.add(informationDetailEntity);
        }
    }


    @OnClick(R.id.back_img)
    public void onViewClicked() {
        finish();
    }
}
