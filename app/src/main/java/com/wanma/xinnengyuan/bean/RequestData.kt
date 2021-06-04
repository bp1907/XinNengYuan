package com.wanma.xinnengyuan.bean

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
        val tm: String,
//        val id: String,
        val jyy: String,
        val tp: ArrayList<Tp>
)

class Tp(val name: String, val content: String)
