package com.xinlan.callingshow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_DIALOG_PERMISSION = 1001;

    Button btnOpen;
    TextView tvDesc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnOpen = findViewById(R.id.btn_open);
        tvDesc = findViewById(R.id.tv_desc);

        initPermission();

        getCanDrawOverlays();

        // 设置是否开启应用上层显示
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCanDrawOverlays();
            }
        });

        if(!isIgnoreBatteryOptimizations()){
            requestIgnoreBatteryOptimizations();
        }

        findViewById(R.id.start_btn).setOnClickListener((v)->{
            Intent intent = new Intent(getApplicationContext(), PhoneListenService.class);
            startService(intent);
        });

        findViewById(R.id.close_btn).setOnClickListener((v)->{
            Intent intent = new Intent(getApplicationContext(), PhoneListenService.class);
            stopService(intent);
        });
    }

    public void requestIgnoreBatteryOptimizations() {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置弹窗应用上层显示
     */
    private void setCanDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 由于8.0对系统弹唱权限的限制，需要用户进去设置中找到对应应用设置弹窗权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                //8.0
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
            } else {
                // 6.0、7.0、9.0
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
            }
        }
    }

    /**
     * 获取设置中应用上层显示状态
     */
    private void getCanDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                // 当前无权限，请授权
                tvDesc.setText("未开启");
            } else {
                tvDesc.setText("已开启");
            }
        }
    }

    /**
     * 权限请求
     * ## 9.0手机状态读取权限说明
     * 要从手机状态中读取电话号码，请根据您的用例更新应用以请求必要的权限：
     * 要通过 PHONE_STATE Intent 操作读取电话号码，同时需要 READ_CALL_LOG 权限和 READ_PHONE_STATE 权限。
     * 要从 onCallStateChanged() 中读取电话号码，只需要 READ_CALL_LOG 权限。 不需要 READ_PHONE_STATE 权限。
     */
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Permission.READ_CALL_LOG , Permission.READ_PHONE_STATE} ,101);
        }
//        AndPermission.with(this)
//                .runtime()
//                .permission(Permission.READ_CALL_LOG, Permission.READ_PHONE_STATE)
//                .rationale((Context context, List<String> data, RequestExecutor executor) -> {
//                    //拒绝一次后重试
//                    executor.execute();
//                })
//                .onGranted(permission -> {
//                    //权限调用成功后的回调
//                })
//                .onDenied(permissions -> {
//                    //权限调用失败后的回调
//                    //finish();
//                })
//                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIALOG_PERMISSION) {
            getCanDrawOverlays();
        }
    }


    private boolean isIgnoreBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            }
        }
        return isIgnoring;
    }
}
