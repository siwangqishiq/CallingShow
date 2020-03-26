package com.xinlan.callingshow;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneCallListener extends PhoneStateListener {
    private static final String TAG = "MyPhoneCallListener";
    protected PhoneListener listener;

    public static boolean wasRinging;

    /**
     * 返回电话状态
     * <p>
     * CALL_STATE_IDLE 挂断
     * CALL_STATE_OFFHOOK 通话中
     * CALL_STATE_RINGING 响铃
     */
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        //state 当前状态 incomingNumber
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            // 挂断
            case TelephonyManager.CALL_STATE_IDLE:
                Log.e(TAG, "当前状态：挂断");
                WindowsUtils.hidePopupWindow();
                wasRinging = false;
                break;
            // 通话中
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.e(TAG, "当前状态：通话中 :"+incomingNumber);
                if (!wasRinging) { //播出电话
                    listener.onCallStateRinging(incomingNumber);
                } else { //接听电话
                    WindowsUtils.hidePopupWindow();
                }
                wasRinging = true;
                break;
            // 电话响铃
            case TelephonyManager.CALL_STATE_RINGING:
                Log.e(TAG, "当前状态：响铃中");
                wasRinging = true;
                listener.onCallStateRinging(incomingNumber);
                break;
            default:
                break;
        }
    }

    public void setPhoneListener(PhoneListener phoneListener) {
        this.listener = phoneListener;
    }

    public interface PhoneListener {
        void onCallStateRinging(String number);
    }
}
