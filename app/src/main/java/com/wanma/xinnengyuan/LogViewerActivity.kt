package com.wanma.xinnengyuan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log_viewer.*

class LogViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)

        val logSummary = intent.getStringExtra("log_summary")
        if(!logSummary.isNullOrEmpty()) {
            text_log.text = logSummary
        }
    }
}