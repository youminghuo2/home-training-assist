package com.example.hometrainng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.entity.InformationDetailEntity;
import com.example.hometrainng.tools.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Package com.example.hometrainng.adapter
 * @Description InformationAdapter
 * @CreateDate: 2020/4/16 9:58 AM
 */
public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.MyViewHolder> {

    private LayoutInflater layoutInflater;
    private Context mContext;
    private List<InformationDetailEntity> mList;


    public InformationAdapter(Context context, List<InformationDetailEntity> mList) {
        this.mContext = context;
        this.mList = mList;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.activity_information_detail_recycleview_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.detailsData.setText(Utils.getDatePoint(mList.get(position).getData()));
        holder.detailsTitle.setText(mList.get(position).getTitle());
        holder.detailsTest.setText(mList.get(position).getMsg());
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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
