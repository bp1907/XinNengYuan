package com.wanma.xinnengyuan.bean.direct

class RequestData(
        val operationinfo: OperationInfo,
        val mainTable: MainTable
)

class OperationInfo(
        val operationDate: String,
        val operator: String,
        val operationTime: String
)

class MainTable(
        val rqsj: String,
        val wxsm: String,
        val bz: String,
        val csry: String,

        val zmztt: ArrayList<Tp>,
        val mp: ArrayList<Tp>,
        val ccu1: ArrayList<Tp>,
        val aqjsjm: ArrayList<Tp>,
        val ccu2: ArrayList<Tp>,
        val bqjsjm: ArrayList<Tp>,
        val cpxx: ArrayList<Tp>,
        val bbxx: ArrayList<Tp>,
        val yzcs: ArrayList<Tp>,
        val csbg: ArrayList<Tp>

)

class Tp(val name: String, val content: String)
