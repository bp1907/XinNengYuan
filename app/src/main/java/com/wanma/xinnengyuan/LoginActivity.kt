package com.wanma.xinnengyuan

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.wanma.xinnengyuan.`interface`.WebServiceCallback
import com.wanma.xinnengyuan.utils.Config
import com.wanma.xinnengyuan.utils.SharePreferenceUtil
import com.wanma.xinnengyuan.utils.WebServiceUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    lateinit var mdialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initData()
    }

    private fun initData() {
        //4796,1-6
        val oaNo = SharePreferenceUtil.get(Config.OA_NO)
        val oaPwd = SharePreferenceUtil.get(Config.OA_PWD)
        oa_no.setText(oaNo)
        oa_pwd.setText(oaPwd)
    }

    fun login(view: View) {
        if(oa_no.text.toString().isNotEmpty() && oa_pwd.text.toString().isNotEmpty()) {

            mdialog = ProgressDialog.show(this, "", "登录中...")

            thread {
                //调用WebService的login方法
                WebServiceUtil.login(oa_no.text.toString(),
                    oa_pwd.text.toString(),
                    0,
                    "127.0.0.1",
                    object : WebServiceCallback{
                        override fun onSuccess(result: String) {
                           runOnUiThread {
                               if(mdialog.isShowing) {
                                   mdialog.dismiss()
                               }
                               Toast.makeText(this@LoginActivity, "登陆成功", Toast.LENGTH_SHORT).show()
                               SharePreferenceUtil.put(Config.OA_NO, oa_no.text.toString())
                               SharePreferenceUtil.put(Config.OA_PWD, oa_pwd.text.toString())
                               val intent = Intent(this@LoginActivity, MainActivity::class.java)
                               startActivity(intent)
                           }
                        }

                        override fun onFailure(e: Exception) {
                            runOnUiThread {
                                if(mdialog.isShowing) {
                                    mdialog.dismiss()
                                }
                                Toast.makeText(this@LoginActivity, e.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    }
}