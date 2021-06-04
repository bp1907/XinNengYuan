package com.wanma.xinnengyuan.`interface`

interface WebServiceCallback {

    fun onSuccess(result: String)

    fun onFailure(e: Exception)
}