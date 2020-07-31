package com.example.hometrainng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.entity.CommentDetailEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private LayoutInflater layoutInflater;
    private Context mContext;
    private List<CommentDetailEntity> mList;

    public CommentAdapter(Context context, List<CommentDetailEntity> mList) {
        this.mContext = context;
        this.mList = mList;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_comment_detail_recycleview_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.commentsDate.setText(mList.get(position).getDate());
        holder.commentsContent.setText(mList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comments_date)
        TextView commentsDate;
        @BindView(R.id.comments_content)
        TextView commentsContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
