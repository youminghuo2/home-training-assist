package com.example.hometrainng.activity.ui.tab5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.hometrainng.R;
import com.example.hometrainng.activity.AttentionActivity;
import com.example.hometrainng.activity.HelpActivity;
import com.example.hometrainng.activity.StatuteActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Tab5Fragment extends Fragment {

    private Tab5ViewModel tab5ViewModel;
    private Unbinder unbinder;
    private Context mContext;
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.btn3)
    Button btn3;
    @BindView(R.id.btn4)
    Button btn4;
    @BindView(R.id.btn5)
    Button btn5;
    @BindView(R.id.btn6)
    Button btn6;

    public static Tab5Fragment newInstance() {
        return new Tab5Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        tab5ViewModel = ViewModelProviders.of(this).get(Tab5ViewModel.class);
        View root = inflater.inflate(R.layout.tab5_fragment, container, false);
        unbinder = ButterKnife.bind(this, root);
        mContext = getActivity();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                startActivity(new Intent(mContext, AttentionActivity.class));
                break;
            case R.id.btn2:
                startActivity(new Intent(mContext, StatuteActivity.class));
                break;
            case R.id.btn3:
                startActivity(new Intent(mContext, HelpActivity.class));
                break;
        }
    }
}
