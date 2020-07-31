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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> implements View.OnClickListener {

    private LayoutInflater layoutInflater;
    private List<String> mList;

    public ChatAdapter(Context context, List<String> list) {
        this.mList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.dateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd_HH:mm")));
        holder.message.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public void addMessage(String message) {
        int position = getItemCount();
        mList.add(position, message);
        notifyItemInserted(position);
    }


    static class ChatViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chat_create_datetime)
        TextView dateTime;
        @BindView(R.id.chat_message)
        TextView message;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
