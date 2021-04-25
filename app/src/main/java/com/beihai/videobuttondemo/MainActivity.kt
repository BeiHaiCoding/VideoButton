package com.beihai.videobuttondemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.beihai.videobutton.VideoButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val videoButton = findViewById<VideoButton>(R.id.videoButton)
        videoButton.setOnRecordListener(object :VideoButton.OnRecordListener{
            override fun onRecordFinished() {
                Toast.makeText(this@MainActivity,"录像结束",Toast.LENGTH_SHORT).show()
            }
        })

    }
}