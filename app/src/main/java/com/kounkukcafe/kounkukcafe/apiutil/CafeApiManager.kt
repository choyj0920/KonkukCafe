package com.kounkukcafe.kounkukcafe.apiutil

import android.annotation.SuppressLint
import android.util.Log
import android.widget.TextView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

data class EmotionBody(val emotion: String)

interface CafeApiService {

    @POST("getCafe")
    fun getCafelistfromEmotion(
        @Body emotionBody: EmotionBody
    ): Call<CafeResponseData?>


}


// 해당 클래스는 싱글톤패턴 클래스로 getinstance로 가져와야함
object CafeApiManager {

    val emotionOrder = listOf("분노", "싫음", "두려움", "행복", "슬픔", "놀람", "중립")

    // 카페 데이터 baseURl
    private val cafeApiBaseUrl = "http://54.180.90.246:3005/"


    private val cafeApiclient = OkHttpClient.Builder().build()
    var cafeApiService: CafeApiService

    init {

        cafeApiService = Retrofit.Builder()
            .baseUrl(cafeApiBaseUrl)
            .client(cafeApiclient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CafeApiService::class.java)
    }




    // emotion
    fun getCafedata(emotion:Any): List<Cafe>? {
        var _emotion=""
        if(emotion is Int && emotion.toInt()<6){
            _emotion=emotionOrder[emotion.toInt()]
        }else if (emotion is String && emotionOrder.contains(emotion.toString())){
            _emotion= emotion.toString()
        }else{
            Log.d("E","오류발생 :카페리스트를 가져올 감정을 제대로 골라주세요")
            return null
        }

        var result:List<Cafe>?=null

        val call = cafeApiService.getCafelistfromEmotion(
            EmotionBody(_emotion)
        )

        call.enqueue(object : Callback<CafeResponseData?> {
            override fun onResponse(
                call: Call<CafeResponseData?>,
                response: Response<CafeResponseData?>
            ) {

                result= response.body()?.cafelist!!

            }

            override fun onFailure(call: Call<CafeResponseData?>, t: Throwable) {

                // 에러 처리
            }
        })
        return result
    }



}


