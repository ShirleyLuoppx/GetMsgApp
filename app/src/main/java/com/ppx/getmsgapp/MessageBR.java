package com.ppx.getmsgapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author: LuoXia
 * @CreateDate: 2021/6/28 22:51
 * @Description:
 */
public class MessageBR extends BroadcastReceiver {

    private String phone = "";
    private String message = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        //2,获取短信内容
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        //3,循环遍历短信过程
        for (Object object : objects) {
            //4,获取短信对象
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
            //5,获取短信对象的基本信息
            if (sms != null) {

                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String phoneNumber1 = tm.getLine1Number();

                phone = sms.getOriginatingAddress();
                message = sms.getMessageBody();
                Log.d("ippx", "onReceive: 短信内容：" + message + "，來自：" + phone);
                EventBus.getDefault().post(new GetMessageBean(phone, message));
            } else {
                Log.d("ippz", "onReceive: sms對象是空的");
            }
        }
    }

    public class GetMessageBean {
        private String phone;
        private String message;

        public GetMessageBean(String phone, String message) {
            this.phone = phone;
            this.message = message;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
