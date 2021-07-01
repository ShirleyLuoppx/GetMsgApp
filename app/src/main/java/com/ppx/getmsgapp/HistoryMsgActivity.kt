package com.ppx.getmsgapp

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

/**
 * @Author: LuoXia
 * @Date: 2021/7/1 17:37
 * @Description: 历史短信
 */
class HistoryMsgActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_msg)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }
    }

}