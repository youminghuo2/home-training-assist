package com.example.hometrainng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hometrainng.R;
import com.example.hometrainng.entity.CardViewEntity;
import com.example.hometrainng.model.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Package com.example.hometrainng.adapter
 * @Description mainHome的adapter适配器
 * @CreateDate: 2020/4/14 11:50 AM
 */
public class MainHomeAdapter extends RecyclerView.Adapter<MainHomeAdapter.MyViewHolder> implements View.OnClickListener {

    private LayoutInflater inflater;
    private Context mContext;
    private List<CardViewEntity> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public MainHomeAdapter(Context context, List<CardViewEntity> list) {
        this.mContext = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_recycleview_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.textView.setText("   " + mList.get(position).getName());
        Glide.with(mContext).load(mList.get(position).getImgUrl()).into(holder.imageView);
        holder.itemView.setOnClickListener(view -> onItemClickListener.onItemClick(view, position));
        if (mList.get(position).getWatched().equals("yes")) {
            holder.WatchVideoImg.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onClick(View view) {
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview_item_img)
        ImageView imageView;
        @BindView(R.id.cardview_item_tv)
        TextView textView;
        @BindView(R.id.watch_video_img)
        ImageView WatchVideoImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
