package com.wanma.xinnengyuan

import android.app.Application
import android.content.Context
import com.tencent.bugly.crashreport.CrashReport
import com.wanma.xinnengyuan.utils.Config
import com.wanma.xinnengyuan.utils.CrashHandle

class MyApplication : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
//        CrashHandle.getInstance(this)

        CrashReport.initCrashReport(this, Config.APP_ID, false)
    }
}