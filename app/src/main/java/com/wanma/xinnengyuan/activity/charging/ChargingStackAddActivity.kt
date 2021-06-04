package com.wanma.xinnengyuan.activity.charging

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.wanma.xinnengyuan.`interface`.OnRemoveListener
import com.wanma.xinnengyuan.adapter.GridImageAdapter
import com.wanma.xinnengyuan.adapter.LineItemAdapter
import com.wanma.xinnengyuan.bean.LineListItem
import com.wanma.xinnengyuan.bean.RequestHeader
import com.wanma.xinnengyuan.bean.charging.*
import com.wanma.xinnengyuan.net.ApiService
import com.wanma.xinnengyuan.net.ServiceCreator
import com.wanma.xinnengyuan.utils.Config
import com.wanma.xinnengyuan.utils.PhotoViewUtil
import com.wanma.xinnengyuan.utils.SharePreferenceUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_charging_add.*
import kotlinx.android.synthetic.main.activity_direct_add.*
import kotlinx.android.synthetic.main.activity_f_q_c_add.*
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class ChargingStackAddActivity : BaseActivity(), OnAddPicClickListener,
        OnItemClickListener, OnRemoveListener {

    val selectList: ArrayList<ArrayList<LocalMedia>> = ArrayList<ArrayList<LocalMedia>>()
    val adapterList: ArrayList<GridImageAdapter> = ArrayList<GridImageAdapter>()
    lateinit var recyclerViewList: ArrayList<RecyclerView>

    lateinit var lineItemAdapter: LineItemAdapter
    val lineList = ArrayList<LineListItem>()
//    val lineSubmitList = ArrayList<LineListItem>()

    var flag = 0

    var offset = 0

    lateinit var operationInfo: OperationInfo

    lateinit var mainTable: MainTable

    lateinit var requestData: RequestData

    lateinit var requestHeader: RequestHeader

    lateinit var requestBoth: RequestBoth


    //图片集合
    private val cpxxList: ArrayList<Tp> = ArrayList()
    private val bbxxList: ArrayList<Tp> = ArrayList()
    private val pcuList: ArrayList<Tp> = ArrayList()

    private val allList = arrayListOf(cpxxList, bbxxList, pcuList)

    private val tableList = arrayListOf<ArrayList<Tp>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charging_add)

        initList()

        charging_add_oa.text = SharePreferenceUtil.get(Config.OA_NO)

        initLineListRecyclerView()

        add_line.setOnClickListener {
            linearChildLayout.requestFocus()
            addLine()
            offset = linearChildLayout.measuredHeight - charging_add_scroll.measuredHeight
            if(offset < 0) {
                offset = 0
            }

            charging_add_scroll.scrollTo(0, offset)
        }

        charging_add_reset.setOnClickListener {
            charging_add_et_code.setText("")
            for (arrayList in selectList) {
                arrayList.clear()
            }
            for (adapter in adapterList) {
                adapter.pathList.clear()
                adapter.nameList.clear()
                adapter.mList = ArrayList<LocalMedia>()
                adapter.notifyDataSetChanged()
            }

            flag = 0
//            lineSubmitList.clear()
            for (lineListItem in lineList) {
                lineListItem.adapter.pathList.clear()
                lineListItem.adapter.nameList.clear()
                lineListItem.adapter.mList = ArrayList<LocalMedia>()

                lineListItem.noStr = ""

                lineListItem.adapter.notifyDataSetChanged()
            }
            lineList.clear()
            chargingAddListRecyclerView.adapter?.notifyDataSetChanged()
            PhotoViewUtil.clearPicCache()
        }

        charging_add_submit.setOnClickListener {
            Toast.makeText(this, "请点击提交并新建按钮提交数据",  Toast.LENGTH_SHORT).show()
        }

        try {
            disposable = RxView.clicks(charging_add_submit_new)
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

//                        for (lineListItem in lineList) {
//                            if(lineListItem.flag) {
//                                lineSubmitList.add(lineListItem)
//                            }
//                        }

                        for (lineListItem in lineList) {
                            val tempList = lineListItem.adapter.pathList
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

                        val size = it.size - 3
                        for(i in 0 until size) {
                            val tempList = ArrayList<Tp>()
                            tableList.add(tempList)
                        }

                        for(i in 0 until it.size) {
                            if(i < 3) {
                                for(j in 0 until adapterList[i].nameList.size) {
                                    val tp = Tp("http://${adapterList[i].nameList[j]}", "http://isrm.wanmagroup.com/api/common/getFileRes/${it[i][j]}")
                                    allList[i].add(tp)
                                }
                            }else {
                                for(j in 0 until lineList[i-3].adapter.nameList.size) {
                                    val tp = Tp("http://${lineList[i-3].adapter.nameList[j]}", "http://isrm.wanmagroup.com/api/common/getFileRes/${it[i][j]}")
                                    tableList[i-3].add(tp)
                                }
                            }

                        }

                        val time = PhotoViewUtil.getDateAndTime()
                        val md5String = "xnycddFCCED4B2FB884C489FA5D1055B30D871$time"

                        //1 requestData
                        //operationInfo
                        operationInfo = OperationInfo(PhotoViewUtil.getDate(),
                                charging_add_oa.text.toString(),
                                PhotoViewUtil.getTime())

                        //mainTable

                        mainTable = MainTable(charging_add_oa.text.toString(),
                                charging_add_et_code.text.toString(),
                                allList[0], allList[1], allList[2])

                        //detail
//                        val details = arrayOfNulls<Detail>(tableList.size)
//                        for(i in 0 until tableList.size) {
//                            val data = Data(lineList[i].noStr, tableList[i])
//                            val detail = Detail(Operate(), data)
//                            details[i] = detail
//                        }

                        val details = Array(tableList.size) {i ->
                            val data = Data(lineList[i].noStr, tableList[i])
                            Detail(Operate(), data)
                        }

                        requestData = RequestData(operationInfo, mainTable, details)

                        //2 requestHeader
                        requestHeader = RequestHeader("xnycdd",
                                time,
                                PhotoViewUtil.md5(md5String))

                        val requestList = arrayListOf(requestData)
                        requestBoth = RequestBoth(requestList, requestHeader)

                        val jsonObject = JSONObject(Gson().toJson(requestBoth))
                        ServiceCreator.create(ApiService::class.java).addChargingData(jsonObject)
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

    private fun addLine() {
        val list = ArrayList<LocalMedia>()
        val adapter = GridImageAdapter(this, list, object : OnAddPicClickListener {
            override fun onAddPicClick(whitch: Int) {
                PhotoViewUtil.showPop(this@ChargingStackAddActivity, object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        result?.let {
                            lineList[whitch].adapter.list.addAll(it)
                            for (media in it) {
                                lineList[whitch].adapter.pathList.add(media.compressPath)
                                val file = File(media.compressPath)
                                lineList[whitch].adapter.nameList.add(file.name)
                            }
                        }
                        lineList[whitch].adapter.mList = lineList[whitch].adapter.list
                        lineList[whitch].adapter.notifyDataSetChanged()
                    }

                    override fun onCancel() {
                    }

                })
            }
        }, object : OnItemClickListener{
            override fun onItemClick(position: Int, view: View, whicth: Int) {
                if(lineList[whicth].adapter.list.isNotEmpty()) {
                    PictureSelector.create(this@ChargingStackAddActivity).externalPicturePreview(position, lineList[whicth].adapter.list,0)
                }
            }
        }, whitch = flag)
        val lineListItem = LineListItem("", adapter)
        lineList.add(lineListItem)
        //提交数据使用lineSubmitList
        lineItemAdapter.notifyDataSetChanged()
        flag++
    }

    override fun onRemove(position: Int) {
        linearChildLayout.requestFocus()
//        lineList[position].flag = false
        flag--
        lineList.removeAt(position)
        lineItemAdapter.notifyDataSetChanged()
    }

    private fun initLineListRecyclerView() {
        val manager = LinearLayoutManager(this)
        chargingAddListRecyclerView.layoutManager = manager
        lineItemAdapter = LineItemAdapter(this, lineList, this)
        chargingAddListRecyclerView.adapter = lineItemAdapter
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
                    charging_add_et_code.setText(obj?.originalValue)
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
                        adapterList[whitch].nameList.add(media.fileName)
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
        //TODO bug
//        if(selectList[whicth].isNotEmpty()) {
//            PictureSelector.create(this).externalPicturePreview(position, selectList[whicth],0)
//        }
    }

    private fun initList() {
        recyclerViewList = arrayListOf(chargingAddCPXXRecyclerView, chargingAddBBXXRecyclerView, chargingAddPCURecyclerView)

        for (i in 0 until 3) {
            val select: ArrayList<LocalMedia> = ArrayList<LocalMedia>()
            val adapter: GridImageAdapter = GridImageAdapter(this, select,
                    this, this, whitch = i)
            adapterList.add(adapter)
            selectList.add(select)
            PhotoViewUtil.initWidget(recyclerViewList[i], this, adapterList[i])
        }
    }

}