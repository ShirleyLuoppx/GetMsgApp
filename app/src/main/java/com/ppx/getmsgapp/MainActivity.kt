package com.ppx.getmsgapp

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val TAG = "GetMsgActivity"
    private var httpUrl = ""
    private var phone = ""
    private var message = ""
    private lateinit var btn_edit: Button
    private lateinit var btn_sure: Button
    private lateinit var et_http_address: EditText
    private lateinit var tv_phone_content: TextView
    private lateinit var tv_message_content: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        requestPermission()
        clickEvent()
        EventBus.getDefault().register(this)
    }

    private fun initView() {
        btn_edit = findViewById(R.id.btn_edit)
        et_http_address = findViewById(R.id.et_http_address)
        btn_sure = findViewById(R.id.btn_sure)
        tv_phone_content = findViewById(R.id.tv_phone_content)
        tv_message_content = findViewById(R.id.tv_message_content)

        httpUrl = getUrlSp()
        if (httpUrl.isEmpty()) {
            et_http_address.setText("")
        } else {
            et_http_address.setText(httpUrl)
        }
    }

    private fun clickEvent() {

        btn_edit.setOnClickListener { //可编辑状态
            et_http_address.isFocusable = true
            et_http_address.isFocusableInTouchMode = true
            et_http_address.requestFocus()
        }

        btn_sure.setOnClickListener { //不可编辑状态
            et_http_address.isFocusable = false
            et_http_address.isFocusableInTouchMode = false

            httpUrl = et_http_address.text.toString()
            Log.d(TAG, "clickEvent: 配置后的网络请求地址：$httpUrl")

            saveUrlSp(httpUrl)
        }
    }

    //存
    private fun saveUrlSp(url: String) {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("httpUrlSp", Activity.MODE_PRIVATE)
        val sp = sharedPreferences.edit()
        sp.putString("httpUrl", url)
        sp.apply()
    }

    //取
    private fun getUrlSp(): String {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("httpUrlSp", Activity.MODE_PRIVATE)
        return sharedPreferences.getString("httpUrl", "")
    }

    /**
     * 收到短信数据的时候就发送
     */
    private fun sendMsg(phone: String, message: String) {
        val json = JSONObject()
        val markdown = JSONObject()
        json.put("msgtype", "markdown")

        markdown.put(
            "content",
            "<font color='#8b0000'>短信内容</font>\\n ><font color='comment'>$message</font>"
        )
        json.put("markdown", markdown)
        val post = json.toString()

        if (httpUrl.startsWith("https://", true) || httpUrl.startsWith("http://", true)) {
            val user = OkHttpClientUtil.createHttpsPostByjson(httpUrl, post, "application/json")
            Log.d(TAG, "sendMsg: url：$httpUrl  ， post : $post \n 接口请求返回数据：$user")
        } else {
            Log.e(TAG, "sendMsg: http请求格式不正确")
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun funName(event: MessageBR.GetMessageBean) {
        message = event.message
        phone = event.phone

        tv_phone_content.text = phone
        tv_message_content.text = message

        if (httpUrl.isEmpty()) {
            /**
             * 没有默认的地址
             */
//            httpUrl =
//                "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=267748b0-f4ed-49a1-a73c-ad4a22061825"
            Toast.makeText(this, "首次进入应用需要配置应用请求地址哦~", Toast.LENGTH_SHORT).show()
        } else {
            sendMsg(phone, message)
        }
    }

    private fun requestPermission() {
        Log.i(TAG, "requestPermission")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
                1001
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(TAG, "onRequestPermissionsResult granted")
                } else {
                    Log.i(TAG, "onRequestPermissionsResult denied")
                    showWaringDialog()
                }
                return
            }
        }
    }

    private fun showWaringDialog() {
        val dialog: android.app.AlertDialog? = android.app.AlertDialog.Builder(this)
            .setTitle("警告！")
            .setMessage("请前往设置->应用->PermissionDemo->权限中打开相关权限，否则功能无法正常运行！")
            .setPositiveButton(
                "确定",
                DialogInterface.OnClickListener { dialog, which -> // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                    finish()
                }).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}