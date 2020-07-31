package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hometrainng.MyApplication;
import com.example.hometrainng.R;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.entity.ConfirmBooKBean;
import com.example.hometrainng.entity.LoginModel;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CounselingDateActivity extends BaseActivity {

    ConfirmBooKBean confirmBooKBean;
    @BindView(R.id.btn_send_message)
    Button btnSendMessage;
    @BindView(R.id.counseling_date_tv_01)
    TextView counselingDateTv01;
    @BindView(R.id.counseling_time_tv_01)
    TextView counselingTimeTv01;
    @BindView(R.id.counseling_date_tv_02)
    TextView counselingDateTv02;
    @BindView(R.id.counseling_time_tv_02)
    TextView counselingTimeTv02;
    @BindView(R.id.counseling_date_tv_03)
    TextView counselingDateTv03;
    @BindView(R.id.counseling_time_tv_03)
    TextView counselingTimeTv03;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.counseling_date_01)
    LinearLayout linearLayout1;
    @BindView(R.id.counseling_date_02)
    LinearLayout linearLayout2;
    @BindView(R.id.counseling_date_03)
    LinearLayout linearLayout3;
    @BindView(R.id.back_image_button)
    ImageView backImageButton;

    List<String> firstBookDateTime;
    List<String> secondBookDateTime;
    List<String> thirdBookDateTime;

    private Context mContext;

    Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling_date);
        mContext = getApplicationContext();
        ButterKnife.bind(this);
        if (getIntent().hasExtra("ConfirmBooKBean")) {
            initDataFromIntent();
        } else {
            initDataFromSharedPreferences();
        }
    }

    private void initDataFromSharedPreferences() {

        btnSendMessage.setVisibility(View.GONE);
        if (RxSPTool.getString(mContext, Constants.FIRST_BOOK_DATE).length() > 0 && RxSPTool.getString(mContext, Constants.FIRST_BOOK_TIME).length() > 0) {
            line1.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.VISIBLE);
            counselingDateTv01.setText(RxSPTool.getString(mContext, Constants.FIRST_BOOK_DATE));
            counselingTimeTv01.setText(RxSPTool.getString(mContext, Constants.FIRST_BOOK_TIME));
        }
        if (RxSPTool.getString(mContext, Constants.SECOND_BOOK_DATE).length() > 0 && RxSPTool.getString(mContext, Constants.SECOND_BOOK_TIME).length() > 0) {
            line2.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            counselingDateTv02.setText(RxSPTool.getString(mContext, Constants.SECOND_BOOK_DATE));
            counselingTimeTv02.setText(RxSPTool.getString(mContext, Constants.SECOND_BOOK_TIME));
        }
        if (RxSPTool.getString(mContext, Constants.THIRD_BOOK_DATE).length() > 0 && RxSPTool.getString(mContext, Constants.THIRD_BOOK_TIME).length() > 0) {

            linearLayout3.setVisibility(View.VISIBLE);
            counselingDateTv03.setText(RxSPTool.getString(mContext, Constants.THIRD_BOOK_DATE));
            counselingTimeTv03.setText(RxSPTool.getString(mContext, Constants.THIRD_BOOK_TIME));
        }
    }

    private void initDataFromIntent() {
        confirmBooKBean = (ConfirmBooKBean) getIntent().getSerializableExtra("ConfirmBooKBean");

        if (confirmBooKBean.getFirstCandidateDatetime() != null) {
            line1.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.VISIBLE);
            firstBookDateTime = parseDateTime(confirmBooKBean.getFirstCandidateDatetime());
            counselingDateTv01.setText(firstBookDateTime.get(0));
            counselingTimeTv01.setText(firstBookDateTime.get(1));
            RxSPTool.putString(mContext, Constants.FIRST_BOOK_DATE, firstBookDateTime.get(0));
            RxSPTool.putString(mContext, Constants.FIRST_BOOK_TIME, firstBookDateTime.get(1));
        }
        if (confirmBooKBean.getSecondCandidateDatetime() != null) {
            line2.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            secondBookDateTime = parseDateTime(confirmBooKBean.getSecondCandidateDatetime());
            counselingDateTv02.setText(secondBookDateTime.get(0));
            counselingTimeTv02.setText(secondBookDateTime.get(1));
            RxSPTool.putString(mContext, Constants.SECOND_BOOK_DATE, secondBookDateTime.get(0));
            RxSPTool.putString(mContext, Constants.SECOND_BOOK_TIME, secondBookDateTime.get(1));
        }
        if (confirmBooKBean.getThirdCandidateDatetime() != null) {
            linearLayout3.setVisibility(View.VISIBLE);
            thirdBookDateTime = parseDateTime(confirmBooKBean.getThirdCandidateDatetime());
            counselingDateTv03.setText(thirdBookDateTime.get(0));
            counselingTimeTv03.setText(thirdBookDateTime.get(1));
            RxSPTool.putString(mContext, Constants.THIRD_BOOK_DATE, thirdBookDateTime.get(0));
            RxSPTool.putString(mContext, Constants.THIRD_BOOK_TIME, thirdBookDateTime.get(1));
        }
    }

    private List<String> parseDateTime(String bookDatetime) {
        List<String> list = new ArrayList<>();
        String[] bookDatetime2 = bookDatetime.split("~");
        LocalDateTime bookDatetime3 = LocalDateTime.parse(bookDatetime2[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        list.add(bookDatetime3.getMonthValue() + "月" + bookDatetime3.getDayOfMonth() + "日" + "(" + bookDatetime3.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE) + ")");
        list.add(bookDatetime2[0].substring(11, 16) + "~" + bookDatetime2[1].substring(11, 16));
        return list;
    }

    @OnClick(R.id.btn_send_message)
    public void counselingDate(View view) {
        String token = RxSPTool.getString(getApplicationContext(), Constants.TOKEN);

        Call<LoginModel> counselingBook = HttpHelper.getInstance()
                .create(HomeTrainService.class)
                .CounselingBook(token, confirmBooKBean);
        counselingBook.enqueue(new Callback<LoginModel>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {

                if (response.body().getCode() == 102) {
                    Toast.makeText(CounselingDateActivity.this, checkMsg(response.body().getMsg()), Constants.Toast_Length).show();
                    return;
                }
                if (response.body().getCode() == 200) {
                    RxSPTool.putInt(getApplicationContext(), Constants.SCHEDULE_STATUS, 0);
                    showMessage();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {

            }
        });
    }

    private String checkMsg(String message) {
        String tag = message.replace("msg.error.", "");
        return getString(MyApplication.getResourcesId(tag, getApplicationContext()));
    }

    @OnClick(R.id.back_image_button)
    public void backImageButton(View view) {
        finish();
    }

    private void darkenBackground(float bgColor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgColor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    private void showMessage() {
        int screenWidth = RxDeviceTool.getScreenWidth(mContext);
        int screenHeight = RxDeviceTool.getScreenHeight(mContext);
        View contentView = LayoutInflater.from(CounselingDateActivity.this).inflate(R.layout.fragment_counselingmessage, null);
//        popupWindow = new PopupWindow(contentView, screenWidth / 15 * 8, screenHeight / 15 * 8, false);
        int height= RxImageTool.dp2px(500);
        int width=RxImageTool.dp2px(800);
        popupWindow=new PopupWindow(contentView,width,height);
        popupWindow.setOutsideTouchable(false);
        View parentView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);

        darkenBackground(0.5f);
        btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            popupWindow.dismiss();
            Intent intent = new Intent(this, MainHomeActivity.class);
            intent.putExtra("id", 5);
            startActivity(intent);
            finish();

        });
    }


}
