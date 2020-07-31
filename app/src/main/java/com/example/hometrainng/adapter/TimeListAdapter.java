package com.example.hometrainng.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeListAdapter extends RecyclerView.Adapter<TimeListAdapter.TimeListViewHolder> {

    private LayoutInflater layoutInflater;
    private Context mContext;
    private String[] strings = {"8：00", "9：00", "10：00", "11：00", "12：00", "13：00", "14：00", "15：00", "16：00", "17：00", "18：00", "19：00"};


    public TimeListAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TimeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.counseling_time_item, parent, false);
        return new TimeListViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TimeListViewHolder holder, int position) {
        holder.tv_time.setText(strings[position]);
    }

    @Override
    public int getItemCount() {
        return strings.length;
    }

    static class TimeListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_time)
        TextView tv_time;

        TimeListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
