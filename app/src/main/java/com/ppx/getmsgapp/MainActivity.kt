package com.ppx.getmsgapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var httpUrl = ""
    private var phone = ""
    private var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        EventBus.getDefault().register(this)
    }

    /**
     * 收到短信数据的时候就发送
     */
    private fun sendMsg(phone: String, message: String) {
        //创建网络处理的对象
        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .build()

        //post请求来获得数据
        //创建一个RequestBody，存放重要数据的键值对
        val body: RequestBody = FormBody.Builder()
            .add("phone", phone)
            .add("message", message).build()
        //创建一个请求对象，传入URL地址和相关数据的键值对的对象
        val request: Request = Request.Builder()
            .url(httpUrl)
            .post(body).build()

        //创建一个能处理请求数据的操作类
        val call: Call = client.newCall(request)

        //使用异步任务的模式请求数据
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("", "错误信息：$e")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("", response.body.toString())
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun funName(event: GetMsgDemo.GetMessageBean) {
        message = event.message
        phone = event.phone

//        sendMsg(phone, message)

        tv_phone_content.text = phone
        tv_message_content.text = message


    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}