package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.hometrainng.R;
import com.example.hometrainng.adapter.CommentAdapter;
import com.example.hometrainng.db.Comments;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.entity.CommentDetailEntity;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxSPTool;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CounselingCommentActivity extends BaseActivity {

    private static final String TAG = "CounselingCommentActivity";
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.information_img)
    ImageView informationImg;
    @BindView(R.id.information_text)
    TextView informationText;
    @BindView(R.id.image_text)
    TextView imageText;
    @BindView(R.id.comment_detail_recycleView)
    RecyclerView commentDetailRecycleView;
    private List<CommentDetailEntity> commentDetailEntityList = new ArrayList<>();
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling_comment);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
    }


    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        informationText.setText(R.string.counseling_room_btn);

        Therapist therapist = LitePal.where("flag=?", "responsible").findLast(Therapist.class);
        if (therapist != null) {
            String url = Utils.getPhotoUrl(therapist.getPhotoPath(), RxSPTool.getString(mContext, "token"));
            Glide.with(mContext).load(url).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(informationImg);
            imageText.setText(therapist.getLastName() + therapist.getFirstName());
            imageText.setVisibility(View.VISIBLE);
        } else {
            Glide.with(mContext).load(R.mipmap.msg_info_detail).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(informationImg);
        }

        initData();
        initRecyclerView();
    }

    private void initData() {
        List<Comments> commentsList = LitePal.select("*").order("createTime desc").find(Comments.class);

        if (commentsList.size() <= 0) {
            return;
        }
        for (int i = 0; i < commentsList.size(); i++) {
            CommentDetailEntity commentDetailEntity = new CommentDetailEntity();
            commentDetailEntity.setDate(Utils.getDatePoint(commentsList.get(i).getUpdateTime().substring(0, 10)));
            commentDetailEntity.setContent(commentsList.get(i).getRehabilitationComment());
            commentDetailEntityList.add(commentDetailEntity);
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        commentDetailRecycleView.setLayoutManager(layoutManager);
        CommentAdapter adapter = new CommentAdapter(mContext, commentDetailEntityList);
        commentDetailRecycleView.setAdapter(adapter);
    }

    @OnClick(R.id.back_img)
    public void onViewClicked() {
        finish();
    }

}
