package com.example.hometrainng.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.entity.InformationDetailEntity;
import com.example.hometrainng.entity.InformationNoticeEntity;
import com.example.hometrainng.tools.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Package com.example.hometrainng.adapter
 * @Description java类作用描述
 * @CreateDate: 2020/5/22 10:07 AM
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {
    private LayoutInflater layoutInflater;
    private Context mContext;
    private List<InformationNoticeEntity> mList;


    public NoticeAdapter(Context context, List<InformationNoticeEntity> mList) {
        this.mContext = context;
        this.mList = mList;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_information_notice_recycleview_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.detailsData.setText(Utils.getDatePoint(mList.get(position).getData()));
        holder.detailsTitle.setText(mList.get(position).getTitle());
        holder.detailsTest.setText(mList.get(position).getMsg());
        if (!TextUtils.isEmpty(mList.get(position).getUrl())) {
            holder.url_tv.setVisibility(View.VISIBLE);
            holder.url_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.url_tv.setText(mList.get(position).getUrl());
        }
        holder.url_tv.setOnClickListener(view -> {
            String uriMsg = "https://" + Utils.checkUrl(mList.get(position).getUrl());
            Uri uri = Uri.parse(uriMsg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            mContext.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.details_data)
        TextView detailsData;
        @BindView(R.id.details_title)
        TextView detailsTitle;
        @BindView(R.id.details_test)
        TextView detailsTest;
        @BindView(R.id.url_tv)
        TextView url_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
