package com.wanma.xinnengyuan.activity.alternating

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
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
import com.wanma.xinnengyuan.bean.RequestHeader
import com.wanma.xinnengyuan.bean.alternating.*
import com.wanma.xinnengyuan.net.ApiService
import com.wanma.xinnengyuan.net.ServiceCreator
import com.wanma.xinnengyuan.utils.PhotoViewUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_alternating_add.*
import kotlinx.android.synthetic.main.activity_direct_add.*
import kotlinx.android.synthetic.main.activity_f_q_c_add.*
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class AlternatingAddActivity : BaseActivity(), OnAddPicClickListener, OnItemClickListener {

    val selectList: ArrayList<ArrayList<LocalMedia>> = ArrayList<ArrayList<LocalMedia>>()
    val adapterList: ArrayList<GridImageAdapter> = ArrayList<GridImageAdapter>()
    lateinit var recyclerViewList: ArrayList<RecyclerView>

    lateinit var operationInfo: OperationInfo

    lateinit var mainTable: MainTable

    lateinit var requestData: RequestData

    lateinit var requestHeader: RequestHeader

    lateinit var requestBoth: RequestBoth


    //图片集合
    private val djjmList: ArrayList<Tp> = ArrayList()
    private val jsjmList: ArrayList<Tp> = ArrayList()

    private val allList = arrayListOf(djjmList, jsjmList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternating_add)

        PhotoViewUtil.setOAAndDate(alternating_add_oa, alternating_add_date, false)

        initList()

        alternating_add_reset.setOnClickListener {
            alternating_et_code.setText("")
            for (arrayList in selectList) {
                arrayList.clear()
            }
            for (adapter in adapterList) {
                adapter.pathList.clear()
                adapter.nameList.clear()
                adapter.mList = ArrayList<LocalMedia>()
                adapter.notifyDataSetChanged()
            }
            PhotoViewUtil.clearPicCache()
        }

        alternating_add_submit.setOnClickListener {
            Toast.makeText(this, "请点击提交并新建按钮提交数据",  Toast.LENGTH_SHORT).show()
        }

        try {
            disposable = RxView.clicks(alternating_add_submit_new)
                    .throttleFirst(2000, TimeUnit.MICROSECONDS)
                    .doOnNext {
                        mdialog = ProgressDialog.show(this, "", "上传中....")
                    }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        val tempPathList = ArrayList<ArrayList<File>>()

                        for (gridImageAdapter in adapterList) {
                            val tempList = gridImageAdapter.pathList
                            val tempFileList = ArrayList<File>()
                            for (s in tempList) {
                                val file = File(s)
                                tempFileList.add(file)
                            }
                            tempPathList.add(tempFileList)
                        }
                        PhotoViewUtil.uploadFile3(tempPathList)
                    }
                    .flatMap {

                        for(i in 0 until it.size) {
                            for(j in 0 until adapterList[i].nameList.size) {
                                val tp = Tp("http://${adapterList[i].nameList[j]}", "http://isrm.wanmagroup.com/api/common/getFileRes/${it[i][j]}")
                                allList[i].add(tp)
                            }
                        }

                        val time = PhotoViewUtil.getDateAndTime()
                        val md5String = "xnyjlcsFCCED4B2FB884C489FA5D1055B30D871$time"

                        //1 requestData
                        //operationInfo
                        operationInfo = OperationInfo(PhotoViewUtil.getDate(),
                                alternating_add_oa.text.toString(),
                                PhotoViewUtil.getTime())

                        //mainTable

                        mainTable = MainTable(alternating_add_date.text.toString(),
                                alternating_et_code.text.toString(),
                        alternating_add_oa.text.toString(), allList[0], allList[1])

                        requestData = RequestData(operationInfo, mainTable)

                        //2 requestHeader
                        requestHeader = RequestHeader("xnyjlcs",
                                time,
                                PhotoViewUtil.md5(md5String))

                        val requestList = arrayListOf(requestData)
                        requestBoth = RequestBoth(requestList, requestHeader)

                        val jsonObject = JSONObject(Gson().toJson(requestBoth))
                        ServiceCreator.create(ApiService::class.java).addAlternatingData(jsonObject)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                1 -> {
                    val obj = data?.getParcelableExtra<HmsScan>(ScanUtil.RESULT)
                    alternating_et_code.setText(obj?.originalValue)
                }
            }
        }
    }

    override fun onAddPicClick(whitch: Int) {
        PhotoViewUtil.showPop(this, object : OnResultCallbackListener<LocalMedia> {
            override fun onResult(result: MutableList<LocalMedia>?) {
                result?.let {
                    selectList[whitch].addAll(it)
                    for (media in it) {
                        adapterList[whitch].pathList.add(media.compressPath)
                        val file = File(media.compressPath)
                        adapterList[whitch].nameList.add(file.name)
                    }
                }
                adapterList[whitch].mList = selectList[whitch]
                adapterList[whitch].notifyDataSetChanged()
            }

            override fun onCancel() {
            }

        })
    }

    override fun onItemClick(position: Int, view: View, whicth: Int) {
        if(selectList[whicth].isNotEmpty()) {
            PictureSelector.create(this).externalPicturePreview(position, selectList[whicth],0)
        }
    }

    private fun initList() {
        recyclerViewList = arrayListOf(djjmRecyclerView, jsjmRecyclerView)

        for (i in 0 until 2) {
            val select: ArrayList<LocalMedia> = ArrayList<LocalMedia>()
            val adapter: GridImageAdapter = GridImageAdapter(this, select,
                    this, this, whitch = i)
            adapterList.add(adapter)
            selectList.add(select)
            PhotoViewUtil.initWidget(recyclerViewList[i], this, adapterList[i])
        }
    }
}