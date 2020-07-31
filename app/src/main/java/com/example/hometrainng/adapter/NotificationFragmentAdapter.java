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
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.model.OnItemClickListener;
import com.example.hometrainng.tools.Utils;

import org.litepal.LitePal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Package com.example.hometrainng.adapter
 * @Description java类作用描述
 * @CreateDate: 2020/5/11 2:31 PM
 */
public class NotificationFragmentAdapter extends RecyclerView.Adapter<NotificationFragmentAdapter.MyViewHolder> implements View.OnClickListener {

    private LayoutInflater inflater;
    private Context mContext;
    private List<RecordVideo> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public NotificationFragmentAdapter(Context context, List<RecordVideo> list) {
        this.mContext = context;
        this.mList = list;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notifications_fragment_recycleview_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(mContext).load("file://" + mList.get(position).getRecordImg()).into(holder.notificationImg);
        holder.dateTimeTv.setText(Utils.TimeToHHMMSS(mList.get(position).getRecordDate()));

        holder.itemView.setOnClickListener(view -> {
            onItemClickListener.onItemClick(view,position);
        });

        String videoId = String.valueOf(mList.get(position).getVideoId());
        List<Video> videoList = LitePal.select("*").where("videoId = ?", videoId).find(Video.class);
        holder.msgTv.setText(videoList.get(0).getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.notification_img)
        ImageView notificationImg;

        @BindView(R.id.date_time_tv)
        TextView dateTimeTv;
        @BindView(R.id.msg_tv)
        TextView msgTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    @Override
    public void onClick(View view) {

    }

}
