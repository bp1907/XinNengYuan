package com.wanma.xinnengyuan.utils

import android.content.Context
import android.content.SharedPreferences
import com.wanma.xinnengyuan.MyApplication

object SharePreferenceUtil {

    lateinit var sharedPreferences: SharedPreferences

    fun get(getStr: String): String {
        sharedPreferences = MyApplication.context.getSharedPreferences("oa", Context.MODE_PRIVATE)
        return sharedPreferences.getString(getStr, "").toString()
    }

    fun put(putStr: String, value: String) {
        sharedPreferences = MyApplication.context.getSharedPreferences("oa", Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putString(putStr, value)
        edit.apply()
    }
}