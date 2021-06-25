package com.ppx.getmsgapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import org.greenrobot.eventbus.EventBus

/**
 * @Author: LuoXia
 * @Date: 2021/6/25 11:11
 * @Description:短信广播接收器
 */
class GetMsgDemo : BroadcastReceiver() {

    private val TAG = "GetMsgDemo"
    private var phone = ""
    private var message = ""

    override fun onReceive(context: Context?, intent: Intent?) {

        //pdus短信单位pdu
        //解析短信内容
        val pdus = intent?.extras?.get("puds")
        val sms = SmsMessage.createFromPdu(pdus as ByteArray?)
        phone = sms.originatingAddress.toString()
        message = sms.messageBody
        Log.d(TAG, "onReceive: $phone---${message}")

        EventBus.getDefault().post(GetMessageBean(phone, message))
    }

    data class GetMessageBean(
        var phone: String = "",
        var message: String = ""
    )
}