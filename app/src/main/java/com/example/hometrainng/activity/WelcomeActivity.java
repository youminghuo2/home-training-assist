package com.example.hometrainng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hometrainng.R;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.tools.PLog;
import com.tamsiree.rxkit.RxSPTool;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class WelcomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private Timer timer;
    private static final int RC_ALL_PERMISSION = 100;
    private static final String TAG = "WelcomeActivity";

    String[] parmsWrite = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    String[] parmsRead = {Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("WrongConstant")
    @AfterPermissionGranted(RC_ALL_PERMISSION)
    private void methodRequiresPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            Toast.makeText(this, getString(R.string.RC_ALL_PERMISSION), Constants.Toast_Length).show();
            EasyPermissions.requestPermissions(this, getString(R.string.RC_ALL_PERMISSION),
                    RC_ALL_PERMISSION, perms);
        } else {
            WelcomeIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        methodRequiresPermission();
    }

    private void WelcomeIntent() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(RxSPTool.getString(getApplicationContext(), Constants.TOKEN))) {
                    startActivity(new Intent(WelcomeActivity.this, RulesActivity.class));
                    RxSPTool.putBoolean(getApplicationContext(), Constants.FIRST_LOGIN, true);
                    finish();
                } else {
                    startActivity(new Intent(WelcomeActivity.this, MainHomeActivity.class));
                    RxSPTool.putBoolean(getApplicationContext(), Constants.FIRST_LOGIN, false);
                    finish();
                }
            }
        }, 1000);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        PLog.e(TAG,"onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            Toast.makeText(this, getString(R.string.RC_ALL_PERMISSION), Toast.LENGTH_SHORT).show();
        } else if (!EasyPermissions.hasPermissions(this, parmsRead) || !EasyPermissions.hasPermissions(this, parmsWrite)) {
            Toast.makeText(this, getString(R.string.RC_ALL_PERMISSION), Toast.LENGTH_SHORT).show();
        }
    }
}
