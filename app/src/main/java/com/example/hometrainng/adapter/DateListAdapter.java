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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.DateListViewHolder> {

    private LayoutInflater layoutInflater;
    private Context mContext;
    private String[] strings = {"（月）", "（火）", "（水）", "（木）", "（金）", "（土）", "（日）"};
    private ArrayList<String> dates = new ArrayList<>();


    public DateListAdapter(Context context, ArrayList<String> dates) {
        this.mContext = context;
        this.dates = dates;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public DateListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.counseling_date_item, parent, false);
        return new DateListViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull DateListViewHolder holder, int position) {
        holder.tv_date.setText(dates.get(position) + "\n" + strings[position]);
    }

    @Override
    public int getItemCount() {
        return strings.length;
    }

    static class DateListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date)
        TextView tv_date;

        DateListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setDates(ArrayList<String> dates) {
        this.dates = dates;
        notifyDataSetChanged();
    }
}
