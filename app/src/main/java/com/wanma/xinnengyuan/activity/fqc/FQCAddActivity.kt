package com.wanma.xinnengyuan.activity.fqc

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.jakewharton.rxbinding2.view.RxView
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.wanma.xinnengyuan.BaseActivity
import com.wanma.xinnengyuan.R
import com.wanma.xinnengyuan.`interface`.OnAddPicClickListener
import com.wanma.xinnengyuan.`interface`.OnItemClickListener
import com.wanma.xinnengyuan.adapter.GridImageAdapter
import com.wanma.xinnengyuan.bean.*
import com.wanma.xinnengyuan.net.ApiService
import com.wanma.xinnengyuan.net.ServiceCreator
import com.wanma.xinnengyuan.utils.Config
import com.wanma.xinnengyuan.utils.PhotoViewUtil
import com.wanma.xinnengyuan.utils.SharePreferenceUtil
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_f_q_c_add.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class FQCAddActivity : BaseActivity(), OnAddPicClickListener, OnItemClickListener {

    var selectList: ArrayList<LocalMedia> = ArrayList<LocalMedia>()
    lateinit var adapter: GridImageAdapter

    private var picIdList = ArrayList<String>()

    lateinit var operationInfo: OperationInfo

    lateinit var mainTable: MainTable

    private val tpList: ArrayList<Tp> = ArrayList()

    lateinit var requestData: RequestData

    lateinit var requestHeader: RequestHeader

    lateinit var requestBoth: RequestBoth
    private val fileList = ArrayList<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_f_q_c_add)

        initWidget()

        PhotoViewUtil.setOAAndDate(fqc_add_oa, fqc_add_date)

        fqc_add_submit.setOnClickListener {
            Toast.makeText(this, "请点击提交并新建按钮提交数据",  Toast.LENGTH_SHORT).show()
        }

        fqc_add_reset.setOnClickListener {
            et_code.setText("")
            selectList.clear()
            adapter.pathList.clear()
            adapter.nameList.clear()
            adapter.mList = selectList
            adapter.notifyDataSetChanged()
            PhotoViewUtil.clearPicCache()
        }

        try {
            disposable = RxView.clicks(fqc_add_submit_new)
                    .throttleFirst(2000, TimeUnit.MICROSECONDS)
                    .doOnNext {
                        mdialog = ProgressDialog.show(this@FQCAddActivity, "", "上传中....")
                    }
                    .observeOn(Schedulers.io())
                    .flatMap {

                        for (s in adapter.pathList) {
                            val file = File(s)
                            fileList.add(file)
                        }
                        PhotoViewUtil.uploadFile2(fileList)
                    }
                    .flatMap {
                        //图片拼接地址 xxx+/id eg:http://isrm.wanmagroup.com/api/common/getFileRes/1620829847232691
                        for (s in it) {
                            picIdList.add("http://isrm.wanmagroup.com/api/common/getFileRes/$s")
                        }
//                        val md5String = PhotoViewUtil.run {
//                            getOA() + getPWD() + getDateAndTime()
//                        }


                        val time = PhotoViewUtil.getDateAndTime()
                        val md5String = "xnyfqcFCCED4B2FB884C489FA5D1055B30D871$time"


                        //1 requestData
                        //operationInfo
                        operationInfo = OperationInfo(PhotoViewUtil.getDate(),
                                fqc_add_oa.text.toString(),
                                PhotoViewUtil.getTime())

                        //mainTable
                        //tp
                        for(i in 0 until picIdList.size) {
                            val tp = Tp("http://${adapter.nameList[i]}", picIdList[i])
                            tpList.add(tp)
                        }

                        mainTable = MainTable(fqc_add_date.text.toString(),
                                et_code.text.toString(),
                                fqc_add_oa.text.toString(),
                                tpList)

                        requestData = RequestData(operationInfo, mainTable)

                        //2 requestHeader
                        requestHeader = RequestHeader("xnyfqc",
                                time,
                                PhotoViewUtil.md5(md5String))

                        val requestList = arrayListOf(requestData)
                        requestBoth = RequestBoth(requestList, requestHeader)
                        val jsonObject = JSONObject(Gson().toJson(requestBoth))

                        ServiceCreator.create(ApiService::class.java).addFQCData(jsonObject)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnSubscribe {
//                    }
//                    .doFinally {
//                        if(mdialog.isShowing) {
//                            mdialog.dismiss()
//                        }
//                    }
                    .doOnNext {
                        if(mdialog.isShowing) {
                            mdialog.dismiss()
                        }
                    }
                    .subscribe {
                        when(it.status) {
                            "1" -> {
                                Toast.makeText(this, "数据插入成功", Toast.LENGTH_SHORT).show()
                                PhotoViewUtil.clearPicCache()
                                finish()
                            }
                            "2" -> {
                                Toast.makeText(this, "数据插入成功，但有异常 ${it.result}", Toast.LENGTH_SHORT).show()
                                PhotoViewUtil.clearPicCache()
                                finish()
                            }
                            "3" -> Toast.makeText(this, "数据插入失败 ${it.result}", Toast.LENGTH_SHORT).show()
                            "4" -> Toast.makeText(this, "数据插入失败，系统接口有异常 ${it.result}", Toast.LENGTH_SHORT).show()
                        }
                    }
        }catch (e: Exception) {
            e.printStackTrace()
        }


//        try {
//            disposable = RxView.clicks(fqc_add_submit_new)
//                    .throttleFirst(2000, TimeUnit.MICROSECONDS)
//                    .observeOn(Schedulers.io())
//                    .flatMap {
//                        Observable.fromIterable(adapter.pathList)
//                    }
//                    .flatMap {
//                        val file = File(it)
//                        when {
//                            !file.exists() || !file.isFile -> {}
//                        }
//                        PhotoViewUtil.uploadFile(file)
//                    }
//
//                    .flatMap {
//                        //图片拼接地址 xxx+/id eg:http://isrm.wanmagroup.com/api/common/getFileRes/1620829847232691
//                        picIdList.add("http://isrm.wanmagroup.com/api/common/getFileRes/${it.data.ctBaseFile.id.toString()}")
//
//
//                        val md5String = PhotoViewUtil.run {
//                            getOA() + getPWD() + getDateAndTime()
//                        }
//
//                        //1 requestData
//                        //operationInfo
//                        operationInfo = OperationInfo(PhotoViewUtil.getDate(),
//                                fqc_add_oa.toString(),
//                                PhotoViewUtil.getTime())
//
//                        //mainTable
//                        //tp
//                        for(i in 0 until picIdList.size) {
//                            val tp = Tp("http://${adapter.nameList[i]}", picIdList[i])
//                            tpList.add(tp)
//                        }
//
//                        mainTable = MainTable(fqc_add_date.toString(),
//                        et_code.text.toString(),
//                        fqc_add_oa.text.toString(),
//                        tpList)
//
//                        requestData = RequestData(operationInfo, mainTable)
//
//                        //2 requestHeader
//                        requestHeader = RequestHeader(fqc_add_oa.toString(),
//                                PhotoViewUtil.getDateAndTime(),
//                                PhotoViewUtil.md5(md5String))
//
//                        requestBoth = RequestBoth(requestData, requestHeader)
//                        ServiceCreator.create(ApiService::class.java).addFQCData(requestBoth)
//                    }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe {
//                        when(it.status) {
//                            "1" -> Toast.makeText(this, "数据插入成功", Toast.LENGTH_SHORT).show()
//                            "2" -> Toast.makeText(this, "数据插入成功，但有异常", Toast.LENGTH_SHORT).show()
//                            "3" -> Toast.makeText(this, "数据插入失败", Toast.LENGTH_SHORT).show()
//                            "4" -> Toast.makeText(this, "数据插入失败，系统接口有异常", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
//        }catch (e: Exception) {
//            e.printStackTrace()
//        }

    }

    //扫码获取条码
    fun getScan(view: View) {
        val permissionList = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        PhotoViewUtil.requestPermission(this, permissionList, 1, 0)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if(permissions == null || grantResults == null) {
            return
        }
        if (grantResults.size < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return
        }
        if(requestCode == 1) {
            ScanUtil.startScan(this, 1, HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create())
        }

    }


    private fun initWidget() {
        adapter = GridImageAdapter(this, selectList, this, this)
        PhotoViewUtil.initWidget(pictureRecyclerView, this, adapter)
    }

    override fun onAddPicClick(whitch: Int) {
        PhotoViewUtil.showPop(this, object : OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: MutableList<LocalMedia>?) {
                //TODO 存储图片地址
                result?.let {
                    selectList.addAll(it)
                    for (media in it) {
                        adapter.pathList.add(media.compressPath)
                        val file = File(media.compressPath)
                        adapter.nameList.add(file.name)
                    }
                }
                adapter.mList = selectList
                adapter.notifyDataSetChanged()
            }

            override fun onCancel() {
            }

        })
    }

    override fun onItemClick(position: Int, view: View, whicth: Int) {
        if(selectList.isNotEmpty()) {
            PictureSelector.create(this).externalPicturePreview(position, selectList,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val images: List<LocalMedia>
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                1 -> {
                    val obj = data?.getParcelableExtra<HmsScan>(ScanUtil.RESULT)
                    et_code.setText(obj?.originalValue)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!disposable.isDisposed) {
            disposable.dispose()
        }
    }

}