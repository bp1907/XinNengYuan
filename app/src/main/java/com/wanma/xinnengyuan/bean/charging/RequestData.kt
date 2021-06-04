package com.wanma.xinnengyuan.bean.charging

class RequestData(
        val operationinfo: OperationInfo,
        val mainTable: MainTable,
        val detail: Array<Detail>
)

class OperationInfo(
        val operationDate: String,
        val operator: String,
        val operationTime: String
)

class MainTable(
        val csry: String,
        val wxsm: String,
//        val id: String,
        val cpxx: ArrayList<Tp>,
        val bbxx: ArrayList<Tp>,
        val pcu: ArrayList<Tp>
)

class Tp(val name: String, val content: String)

class Detail(val operate: Operate, val data: Data)

class Operate(val action: String = "SaveOrUpdate", val actionDescribe: String = "Save")

class Data(
        val ccubh: String,
        val tp: ArrayList<Tp>
)
