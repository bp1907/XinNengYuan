package com.wanma.xinnengyuan.net

import com.wanma.xinnengyuan.bean.BackResponse
import com.wanma.xinnengyuan.bean.BaseResponse
import com.wanma.xinnengyuan.bean.RequestBoth
import io.reactivex.Observable
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.http.*
import java.util.*

/**
 * author: wanma
 * Date: 2021/1/26
 * Description
 */
interface ApiService {
//    @Headers("Content-Type: multipart/form-data"/*,"Authorization: SK:SRM12345678"*/)
    @Multipart
    @POST("base/ctBaseFile/saveFile50")
    fun saveFile(@Part file: MultipartBody.Part, @Query("name") name: String,
    @Query("type") type: String, @Query("size") size: String) : Observable<BaseResponse>


    //FQC添加
    @Headers("url_name: add")
    @POST("api/cube/restful/interface/saveOrUpdateModeData/RESTful_xnyfqcdata")
    fun addFQCData(@Query("datajson") requestBoth: JSONObject) : Observable<BackResponse>

    //OQC添加
    @Headers("url_name: add")
    @POST("api/cube/restful/interface/saveOrUpdateModeData/RESTful_oqcdata")
    fun addOQCData(@Query("datajson") requestBoth: JSONObject) : Observable<BackResponse>

    //直流添加
    @Headers("url_name: add")
    @POST("api/cube/restful/interface/saveOrUpdateModeData/RESTful_xnyzldata")
    fun addDirectData(@Query("datajson") requestBoth: JSONObject) : Observable<BackResponse>

    //交流添加
    @Headers("url_name: add")
    @POST("api/cube/restful/interface/saveOrUpdateModeData/RESTful_xnycssj")
    fun addAlternatingData(@Query("datajson") requestBoth: JSONObject) : Observable<BackResponse>

    //充电堆添加
    @Headers("url_name: add")
    @POST("api/cube/restful/interface/saveOrUpdateModeData/RESTful_xnycdddata")
    fun addChargingData(@Query("datajson") requestBoth: JSONObject) : Observable<BackResponse>
}
