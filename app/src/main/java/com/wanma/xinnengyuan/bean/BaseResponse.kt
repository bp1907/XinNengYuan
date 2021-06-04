package com.wanma.xinnengyuan.bean

import com.google.gson.annotations.SerializedName

/**
 * author: wanma
 * Date: 2021/1/26
 * Description
 */
//数据类
data class BaseResponse(val code: Int, val msg: String, val data: Data)
data class Data(@SerializedName("trans_no") val transNo: String, @SerializedName("dispatch_place") val dispatchPlace: Array<String>, val ctBaseFile: CtBaseFile, val version: String)
data class CtBaseFile(@SerializedName("file_name") val fileName: String, val id: Long)