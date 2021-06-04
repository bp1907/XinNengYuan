package com.wanma.xinnengyuan.utils

import android.widget.Toast
import com.wanma.xinnengyuan.MyApplication
import com.wanma.xinnengyuan.`interface`.WebServiceCallback
import org.ksoap2.SoapEnvelope
import org.ksoap2.SoapFault
import org.ksoap2.SoapFault12
import org.ksoap2.serialization.MarshalBase64
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

object WebServiceUtil {

    private const val URL = "http://172.17.4.250:8081/services/DocService"
    private const val NAME_SPACE = "http://localhost/services/DocService"
    lateinit var methodName: String
    lateinit var soapAction: String

    fun login(loginId: String, password : String, loginType:Int, ipAddress: String, webServiceCallback: WebServiceCallback) {
        methodName = "login"
        soapAction = "$NAME_SPACE/$methodName"
        //创建soapObject对象，指定WebService的命名空间和调用的方法名
        val soapObject = SoapObject(NAME_SPACE, methodName)

        //设置WebService接口传入的参数
        soapObject.addProperty("in0", loginId)
        soapObject.addProperty("in1", password)
        soapObject.addProperty("in2", loginType)
        soapObject.addProperty("in3", ipAddress)

        commonMethod(soapObject, webServiceCallback)
    }

    private fun commonMethod(soapObject: SoapObject, webServiceCallback: WebServiceCallback ) {
        //生成调用WebService方法的soap请求信息，并指定soap版本
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)

        envelope.bodyOut = soapObject

        //设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true

        val transport = HttpTransportSE(URL)

        try {
            //调用WebService
            transport.call(soapAction, envelope)

            //获取返回数据
            val result: String

            result = if(envelope.bodyIn is SoapFault) {
                (envelope.bodyIn as SoapFault).faultstring
            }else {
                val resultObject: SoapObject= envelope.bodyIn as SoapObject
                resultObject.getProperty(0).toString()
            }

            //判断是否返回Session
            if(result.length > 30) {
                webServiceCallback.onSuccess(result)
                SharePreferenceUtil.put(Config.SESSION, result)
            }else {
                Toast.makeText(MyApplication.context, "用户名为空", Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception) {
            webServiceCallback.onFailure(e)
            e.printStackTrace()
            return
        }
    }
}