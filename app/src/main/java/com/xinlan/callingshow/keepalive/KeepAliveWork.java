package com.xinlan.callingshow.keepalive;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.xinlan.callingshow.PhoneListenService;

import java.util.concurrent.TimeUnit;

public class KeepAliveWork extends Worker {
    private static final String TAG = "KeepLiveWork";

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "keep-> doWork: startKeepService");
        //启动job服务
        startJobService();
        //启动相互绑定的服务
        startKeepWork();
        return Result.SUCCESS;
    }

    public void startJobService(){
        Intent intent = new Intent(getApplicationContext(), PhoneListenService.class);
        getApplicationContext().startService(intent);
    }

    public static void startKeepWork() {
        WorkManager.getInstance().cancelAllWorkByTag(TAG);
        Log.d(TAG, "keep-> dowork startKeepWork");
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(KeepAliveWork.class)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                .addTag(TAG)
                .build();
        WorkManager.getInstance().enqueue(oneTimeWorkRequest);
    }
}
