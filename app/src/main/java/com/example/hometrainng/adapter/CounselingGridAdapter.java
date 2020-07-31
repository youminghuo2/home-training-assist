package com.example.hometrainng.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.activity.CounselingGridActivity;
import com.example.hometrainng.entity.TherapistSchedule;
import com.example.hometrainng.tools.DateUtils;
import com.example.hometrainng.tools.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.SneakyThrows;

public class CounselingGridAdapter extends RecyclerView.Adapter<CounselingGridAdapter.CounselingViewHolder> {

    private LayoutInflater layoutInflater;
    private Activity mContext;
    private ArrayList<Integer> positions = new ArrayList<>();
    private ArrayList<TherapistSchedule.DataBean> beans = new ArrayList<>();
    private ArrayList<TherapistSchedule.DataBean> selectIds = new ArrayList<>(3);
    private String CurrentDate;
    private int firstPosition = -1;
    private int secondPosition = -1;
    private int thirdPosition = -1;

    public CounselingGridAdapter(Activity context, ArrayList<Integer> positions, ArrayList<TherapistSchedule.DataBean> beans, String CurrentDate) {
        this.mContext = context;
        this.positions = positions;
        this.beans = beans;
        this.CurrentDate = CurrentDate;
        selectIds.add(null);
        selectIds.add(null);
        selectIds.add(null);
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CounselingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.counseling_grid_item, parent, false);
        return new CounselingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CounselingViewHolder holder, int position) {
        holder.tv_schedule1.setVisibility(View.GONE);
        holder.tv_schedule2.setVisibility(View.GONE);
        if (firstPosition == position * 2) {
            holder.view1.setTag(beans.get(positions.indexOf(position * 2)));
            holder.view1.setVisibility(View.GONE);
            holder.item_empty1.setVisibility(View.GONE);
            holder.tv_schedule1.setVisibility(View.VISIBLE);
            holder.tv_schedule1.setText("1");
        } else if (secondPosition == position * 2) {
            holder.view1.setTag(beans.get(positions.indexOf(position * 2)));
            holder.view1.setVisibility(View.GONE);
            holder.item_empty1.setVisibility(View.GONE);
            holder.tv_schedule1.setVisibility(View.VISIBLE);
            holder.tv_schedule1.setText("2");
        } else if (thirdPosition == position * 2) {
            holder.view1.setTag(beans.get(positions.indexOf(position * 2)));
            holder.view1.setVisibility(View.GONE);
            holder.item_empty1.setVisibility(View.GONE);
            holder.tv_schedule1.setVisibility(View.VISIBLE);
            holder.tv_schedule1.setText("3");
        } else if (positions.contains(position * 2)) {
            holder.view1.setTag(beans.get(positions.indexOf(position * 2)));
            holder.view1.setTag(beans.get(positions.indexOf(position * 2)));
            holder.view1.setVisibility(View.VISIBLE);
            holder.item_empty1.setVisibility(View.GONE);
        } else {
            holder.item_empty1.setVisibility(View.VISIBLE);
            holder.view1.setVisibility(View.GONE);
        }
        if (firstPosition == (position * 2 + 1)) {
            holder.view2.setTag(beans.get(positions.indexOf(position * 2 + 1)));
            holder.view2.setVisibility(View.GONE);
            holder.item_empty2.setVisibility(View.GONE);
            holder.tv_schedule2.setVisibility(View.VISIBLE);
            holder.tv_schedule2.setText("1");
        } else if (secondPosition == (position * 2 + 1)) {
            holder.view2.setTag(beans.get(positions.indexOf(position * 2 + 1)));
            holder.view2.setVisibility(View.GONE);
            holder.item_empty2.setVisibility(View.GONE);
            holder.tv_schedule2.setVisibility(View.VISIBLE);
            holder.tv_schedule2.setText("2");
        } else if (thirdPosition == (position * 2 + 1)) {
            holder.view2.setTag(beans.get(positions.indexOf(position * 2 + 1)));
            holder.view2.setVisibility(View.GONE);
            holder.item_empty2.setVisibility(View.GONE);
            holder.tv_schedule2.setVisibility(View.VISIBLE);
            holder.tv_schedule2.setText("3");
        } else if (positions.contains(position * 2 + 1)) {
            holder.view2.setTag(beans.get(positions.indexOf(position * 2 + 1)));
            holder.view2.setVisibility(View.VISIBLE);
            holder.item_empty2.setVisibility(View.GONE);
        } else {
            holder.item_empty2.setVisibility(View.VISIBLE);
            holder.view2.setVisibility(View.GONE);
        }
        holder.tv_schedule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectIds.contains(holder.view1.getTag())) {
                    int x = selectIds.indexOf(holder.view1.getTag());
                    ((CounselingGridActivity) mContext).setCounselingDate(x + 1, null);
                    switch (x + 1) {
                        case 1:
                            firstPosition = -1;
                            break;
                        case 2:
                            secondPosition = -1;
                            break;
                        case 3:
                            thirdPosition = -1;
                            break;
                        default:
                            break;
                    }
                    selectIds.set(x, null);
                    holder.view1.setVisibility(View.VISIBLE);
                    holder.item_empty1.setVisibility(View.GONE);
                    holder.tv_schedule1.setVisibility(View.GONE);
                }
            }
        });
        holder.tv_schedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectIds.contains(holder.view2.getTag())) {
                    int x = selectIds.indexOf(holder.view2.getTag());
                    ((CounselingGridActivity) mContext).setCounselingDate(x + 1, null);
                    switch (x + 1) {
                        case 1:
                            firstPosition = -1;
                            break;
                        case 2:
                            secondPosition = -1;
                            break;
                        case 3:
                            thirdPosition = -1;
                            break;
                        default:
                            break;
                    }
                    selectIds.set(x, null);
                    holder.view2.setVisibility(View.VISIBLE);
                    holder.item_empty2.setVisibility(View.GONE);
                    holder.tv_schedule2.setVisibility(View.GONE);
                }
            }
        });
        holder.view1.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                if (selectIds.size() <= 3) {
                    int x = -1;
                    for (int i = 0; i < selectIds.size(); i++) {
                        if (selectIds.get(i) == null) {
                            selectIds.set(i, (TherapistSchedule.DataBean) holder.view1.getTag());
                            x = i;
                            break;
                        }
                    }
//                    selectIds.add((TherapistSchedule.DataBean) holder.view1.getTag());
                    if (x >= 0) {
                        String a=Utils.setBaseAECMsg(selectIds.get(x).getTherapistLastName());
                        String b=Utils.setBaseAECMsg(selectIds.get(x).getTherapistFirstName());
                        ((CounselingGridActivity) mContext).setCounselingDate(x + 1, (selectIds.get(x).getScheduleTime()+a+b));
                        holder.view1.setVisibility(View.GONE);
                        holder.item_empty1.setVisibility(View.GONE);
                        holder.tv_schedule1.setVisibility(View.VISIBLE);
                        holder.tv_schedule1.setText(Integer.toString(x + 1));
                    }
                }
            }
        });
        holder.view2.setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                if (selectIds.size() <= 3) {
                    int x = -1;
                    for (int i = 0; i < selectIds.size(); i++) {
                        if (selectIds.get(i) == null) {
                            selectIds.set(i, (TherapistSchedule.DataBean) holder.view2.getTag());
                            x = i;
                            break;
                        }
                    }
                    if (x >= 0) {
//                    selectIds.add((TherapistSchedule.DataBean) holder.view2.getTag());
                        String a=Utils.setBaseAECMsg(selectIds.get(x).getTherapistLastName());
                        String b=Utils.setBaseAECMsg(selectIds.get(x).getTherapistFirstName());
                        ((CounselingGridActivity) mContext).setCounselingDate(x + 1, (selectIds.get(x).getScheduleTime()+a+b));
                        holder.view2.setVisibility(View.GONE);
                        holder.item_empty2.setVisibility(View.GONE);
                        holder.tv_schedule2.setVisibility(View.VISIBLE);
                        holder.tv_schedule2.setText(Integer.toString(x + 1));
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 84;
    }

    static class CounselingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_empty)
        LinearLayout item_empty1;

        @BindView(R.id.item_empty2)
        LinearLayout item_empty2;

        @BindView(R.id.tv_schedule)
        TextView tv_schedule1;

        @BindView(R.id.tv_schedule2)
        TextView tv_schedule2;

        @BindView(R.id.view)
        View view1;

        @BindView(R.id.view2)
        View view2;

        CounselingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void refresh(ArrayList<Integer> positions, ArrayList<TherapistSchedule.DataBean> beans, String CurrentDate) {
        this.positions = positions;
        this.beans = beans;
        this.CurrentDate = CurrentDate;
        if (selectIds.get(0) != null && DateUtils.isDayInWeek(CurrentDate, selectIds.get(0).getScheduleTime().substring(0, 10))) {
            firstPosition = DateUtils.getSchedulePosition(selectIds.get(0).getScheduleTime());
        } else {
            firstPosition = -1;
        }
        if (selectIds.get(1) != null && DateUtils.isDayInWeek(CurrentDate, selectIds.get(1).getScheduleTime().substring(0, 10))) {
            secondPosition = DateUtils.getSchedulePosition(selectIds.get(1).getScheduleTime());
        } else {
            secondPosition = -1;
        }
        if (selectIds.get(2) != null && DateUtils.isDayInWeek(CurrentDate, selectIds.get(2).getScheduleTime().substring(0, 10))) {
            thirdPosition = DateUtils.getSchedulePosition(selectIds.get(2).getScheduleTime());
        } else {
            thirdPosition = -1;
        }
        notifyDataSetChanged();
    }

    public ArrayList<TherapistSchedule.DataBean> getSelectIds() {
        return selectIds;
    }
}
