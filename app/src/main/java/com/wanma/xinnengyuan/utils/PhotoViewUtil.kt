package com.wanma.xinnengyuan.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.tools.PictureFileUtils
import com.wanma.xinnengyuan.GlideEngine
import com.wanma.xinnengyuan.MyApplication
import com.wanma.xinnengyuan.R
import com.wanma.xinnengyuan.adapter.GridImageAdapter
import com.wanma.xinnengyuan.bean.BaseResponse
import com.wanma.xinnengyuan.net.ApiService
import com.wanma.xinnengyuan.net.ServiceCreator
import com.wanma.xinnengyuan.view.FullyGridLayoutManager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.experimental.and

object PhotoViewUtil {

    private const val maxSelectNum = 8
    lateinit var pop: PopupWindow

    fun initWidget(pictureRecyclerView: RecyclerView, context: Context, adapter: GridImageAdapter, spanCount: Int = 4) {
        val manager = FullyGridLayoutManager(context, spanCount, GridLayoutManager.VERTICAL, false)
        pictureRecyclerView.layoutManager = manager
        pictureRecyclerView.adapter = adapter
    }

    fun showPop(context: Activity, onResultCallbackListener: OnResultCallbackListener<LocalMedia>) {
        val bottomView = View.inflate(context, R.layout.layout_bottom_dialog, null)
        val album = bottomView.findViewById<TextView>(R.id.tv_album)
        val camera = bottomView.findViewById<TextView>(R.id.tv_camera)
        val cancel = bottomView.findViewById<TextView>(R.id.tv_cancel)
        pop = PopupWindow(bottomView, -1, -2)
        pop.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pop.isOutsideTouchable = true
        pop.isFocusable = true
        val layoutParams = context.window.attributes
        layoutParams.alpha = 0.5f
        context.window.attributes = layoutParams
        pop.setOnDismissListener {
            val layoutParams = context.window.attributes
            layoutParams.alpha = 1f
            context.window.attributes = layoutParams
        }
        pop.animationStyle = R.style.main_menu_photo_anim
        pop.showAtLocation(context.window.decorView, Gravity.BOTTOM, 0, 0)

        val clickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.tv_album ->                         //相册
                    PictureSelector.create(context)
                        .openGallery(PictureMimeType.ofImage())
                        .imageEngine(GlideEngine.createGlideEngine())
                        .maxSelectNum(maxSelectNum)
                        .minSelectNum(1)
                        .imageSpanCount(4)
                            .compress(true)
                            .renameCompressFile("${System.currentTimeMillis()}.jpg")
                        .selectionMode(PictureConfig.MULTIPLE)
                        .forResult(onResultCallbackListener)
                R.id.tv_camera ->                         //拍照
                    PictureSelector.create(context)
                        .openCamera(PictureMimeType.ofImage())
                        .imageEngine(GlideEngine.createGlideEngine())
                            .compress(true)
                            .renameCompressFile("${System.currentTimeMillis()}.jpg")
                        .forResult(onResultCallbackListener)
                R.id.tv_cancel -> {
                }
            }
            if(pop.isShowing) {
                pop.dismiss()
            }
        }

        album.setOnClickListener(clickListener)
        camera.setOnClickListener(clickListener)
        cancel.setOnClickListener(clickListener)
    }


    //清空图片缓存，包括裁剪、压缩的图片，避免OOM--上传完成后调用，必须获取权限
    fun clearPicCache() {
        PictureFileUtils.deleteCacheDirFile(MyApplication.context, PictureMimeType.ofImage())
    }

    fun requestPermission(activity: Activity, permissions: Array<String>, requestCode: Int, mode: Int) {
        ActivityCompat.requestPermissions(
                activity,
                permissions,
                requestCode
        )
    }

    fun uploadFile(file: File): Observable<BaseResponse> {
        val fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

        val part =  MultipartBody.Part.createFormData("picture", file.name, fileRequestBody);

//        mdialog =
//                ProgressDialog.show(this@MainActivity, "", "上传中....")

        return ServiceCreator.create(ApiService::class.java)
            .saveFile(part, file.name, "image/png", file.length().toString())
    }

    fun uploadFile2(fileList: ArrayList<File>): Observable<ArrayList<String>> {
        val list = ArrayList<String>()
        for (file in fileList) {
            val fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

            val part =  MultipartBody.Part.createFormData("picture", file.name, fileRequestBody);

            ServiceCreator.create(ApiService::class.java)
                    .saveFile(part, file.name, "image/png", file.length().toString())
                    .subscribe {
                        list.add(it.data.ctBaseFile.id.toString())
                    }
        }
        return Observable.fromArray(list)

    }

    fun uploadFile3(fileList: ArrayList<ArrayList<File>>): Observable<ArrayList<ArrayList<String>>> {
        val list = ArrayList<ArrayList<String>>()
        for (arrayList in fileList) {
            val tempList = ArrayList<String>()
            for (file in arrayList) {
                val fileRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)

                val part =  MultipartBody.Part.createFormData("picture", file.name, fileRequestBody);

                ServiceCreator.create(ApiService::class.java)
                        .saveFile(part, file.name, "image/png", file.length().toString())
                        .subscribe {
                            tempList.add(it.data.ctBaseFile.id.toString())
                        }
            }
            list.add(tempList)
        }
        return Observable.fromArray(list)

    }


    /**
     * 设置oa账号和当前时间
     */
    @SuppressLint("SimpleDateFormat")
    fun setOAAndDate(oa: TextView, date: TextView, flag: Boolean = true) {
        oa.text = SharePreferenceUtil.get(Config.OA_NO)
        if(flag) {
            date.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        }else {
            date.text = SimpleDateFormat("yyyy-MM-dd").format(Date())
        }

    }

    fun getOA() = SharePreferenceUtil.get(Config.OA_NO)
    fun getPWD() = SharePreferenceUtil.get(Config.OA_PWD)

    @SuppressLint("SimpleDateFormat")
    fun getDate() = SimpleDateFormat("yyyy-MM-dd").format(Date())

    @SuppressLint("SimpleDateFormat")
    fun getTime() = SimpleDateFormat("HH:mm:ss").format(Date())

    @SuppressLint("SimpleDateFormat")
    fun getDateAndTime() = SimpleDateFormat("yyyyMMddHHmmss").format(Date())

    //md5加密  oa+password+currentDateTime
    fun md5(str: String): String {
        if(TextUtils.isEmpty(str)) {
            return ""
        }
        try {
            val md5: MessageDigest = MessageDigest.getInstance("MD5")
            val bytes = md5.digest(str.toByteArray())
            var result: StringBuilder = StringBuilder()
            for (byte in bytes) {
                //获取低八位有效值,将整数转化为16进制
                var hexString = Integer.toHexString(byte.toInt() and 0xff)
                if(hexString.length < 2) {
                    hexString = "0$hexString"
                }
                result.append(hexString)
            }
            return result.toString()
        }catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

}