package com.wanma.xinnengyuan.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.core.content.pm.PackageInfoCompat
import com.wanma.xinnengyuan.LogViewerActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class CrashHandle private constructor(context: Context) : Thread.UncaughtExceptionHandler{

    //上下文信息
    private val mContext = context

    //用于存放参数信息
    private val info = LinkedHashMap<String, String>()

    //用于格式化日期
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm;ss", Locale.getDefault())

    //单例模式
    companion object {
        private var instance: CrashHandle? = null
        fun getInstance(context: Context): CrashHandle? {
            if(instance == null) {
                synchronized(CrashHandle::class) {
                    instance = CrashHandle(context)
                }
            }
            return instance
        }
    }

    //构造初始化
    init {
        //设置当前类为应用默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    //当uncaughtException发生时调用
    override fun uncaughtException(t: Thread, e: Throwable) {
        handleException(e)
        try {
            Thread.sleep(2000)
        }catch (e: Exception) {
        }
        exitProcess(0)
    }

    //自定义错误处理
    private fun handleException(e: Throwable) {
        thread {
            Looper.prepare()
            Toast.makeText(mContext, "A problem caused", Toast.LENGTH_SHORT).show()
            Looper.loop()
        }

        //跳转到日志显示界面
        val intent = Intent(mContext, LogViewerActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("log_summary", getLogSummary(e))
        mContext.startActivity(intent)

        //这里可以执行一些业务操作,比如保存崩溃日志到文件等等
    }

    //获取日志概要
    private fun getLogSummary(e: Throwable): String? {
        val sb = getLogHeader().append("\n")
        sb.append("异常类: ${e.javaClass}\n")
        sb.append("异常信息: ${e.message}\n\n")
        for (i in e.stackTrace.indices) {
            sb.append("****堆栈追踪 ${i + 1}\n")
            sb.append("类名: ${e.stackTrace[i].className}\n")
            sb.append("方法: ${e.stackTrace[i].methodName}\n")
            sb.append("文件: ${e.stackTrace[i].fileName}\n")
            sb.append("行数: ${e.stackTrace[i].lineNumber}\n\n")
        }
        return sb.toString().trim()
    }

    //获取日志头信息
    private fun getLogHeader(): StringBuffer {
        val sb = StringBuffer()
        sb.append(">>>>时间: ${mDateFormat.format(Date())}\n")
        putInfoToMap(mContext)
        info.entries.forEach {
            sb.append("${it.key}: ${it.value}\n")
        }
        return sb
    }

    //收集参数信息
    private fun putInfoToMap(context: Context) {
        info["设备型号"] = Build.MODEL
        info["设备品牌"] = Build.BOARD
        info["硬件名称"] = Build.HARDWARE
        info["硬件制造商"] = Build.MANUFACTURER
        info["系统版本"] = Build.VERSION.RELEASE
        info["系统版本号"] = "${Build.VERSION.SDK_INT}"

        val pm = context.packageManager
        val pi = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
        if(pi != null) {
            info["应用版本"] = pi.versionName
            info["应用版本号"] = "${PackageInfoCompat.getLongVersionCode(pi)}"
        }
    }

}