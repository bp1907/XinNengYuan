package com.wanma.xinnengyuan.bean.alternating

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
        val csry: String,

        val djjm: ArrayList<Tp>,
        val jsjm: ArrayList<Tp>
)

class Tp(val name: String, val content: String)
