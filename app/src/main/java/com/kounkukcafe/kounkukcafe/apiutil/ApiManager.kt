package com.kounkukcafe.kounkukcafe.apiutil

import android.util.Log
import android.widget.TextView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface EmotionApiService {
    @Multipart
    @POST("emotion/er/v1/recognition")
    fun recognizeEmotion(
        @Part("config") config: RequestBody,
        @Header("Authorization") auth: String,
        @Header("x-api-key") apiKey: String,
        @Header("Cookie") cookie: String
    ): Call<EmotionRecognitionResponse?>
}
interface TokenApiService {
    @POST("v1/cognito")
    fun getToken(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") content_Type: String): Call<TokenResponse?>
}

// 해당 클래스는 싱글톤패턴 클래스로 getinstance로 가져와야함
object ApiManager {


    // 토큰 발급용 Authorization - 토큰 발급에만 쓰이는 Clienid/password 인코딩한거
    private val tokenauthorization ="Basic N2wyYjNmbG5zdnUwMWVsNGg3N3M2OTBqdTY6MWMxa3VvNWF1bDB0M2ZqcTZqdTZlc3A0ZGg5bjlsaGE5a3N0YzduNmwyY2VvYW1rZW12cA=="
    // 토큰 발급용 baseURl
    private val tokenApiBaseUrl = "https://oauth.api.lgthinqai.net:443/"
    // 감정인식용 baseURL
    private val emotionApiBaseUrl = "https://korea.api.lgthinqai.net:443/"
    // 감정인식 api-key
    private val emotionapikey="MTtlZjUyYzFmYTViMjc0NGNkYTg3NDE3NGYwOWU5NGQ0YTsxNjgxMDk2OTAzMzk1"


    private var token: String? = null
    private var expirationTime: Long = 0

    private val tokenclient = OkHttpClient.Builder().build()
    lateinit var tokenapiService: TokenApiService

    // emotion recog 관련
    private val emotionclient = OkHttpClient.Builder().build()
    lateinit var emotionapiService: EmotionApiService

    init {
        emotionapiService = Retrofit.Builder()
            .baseUrl(emotionApiBaseUrl)
            .client(emotionclient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmotionApiService::class.java)

        tokenapiService = Retrofit.Builder()
            .baseUrl(tokenApiBaseUrl)
            .client(tokenclient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenApiService::class.java)
    }

    //토큰 체크
    fun checkToken(): String? {
        if (System.currentTimeMillis() < expirationTime) {
            return token
        }
        return null
    }


    // 토큰 갱신 -만들어는 뒀는데 감정인식에 이미 작성되어있어서 쓸모없음
    fun getToken() {

        val call = tokenapiService.getToken(
            tokenauthorization,
            "application/x-www-form-urlencoded",
        )

        call.enqueue(object : Callback<TokenResponse?> {
            override fun onResponse(
                call: Call<TokenResponse?>,
                response: Response<TokenResponse?>
            ) {

                token= response.body()?.access_token
                val expiration = System.currentTimeMillis() + 50 * 60 * 1000 // 50분
                expirationTime = expiration
            }

            override fun onFailure(call: Call<TokenResponse?>, t: Throwable) {

                // 에러 처리
            }
        })
    }

    // 감정인식 inputtext넣고 , 텍스트뷰 넣으면,감정인식 결과 그 텍스트뷰에 글 뿌려줌
    fun callEmotionrecognition(inputText: String,tv:TextView) {
        // emotion api 작성
        val config = "{\"type\": \"EMOTION_RECOGNITION\",\"input\": {\"type\": \"TEXT\", \"text\": \"${inputText}\" }}".toRequestBody("text/plain".toMediaType())


        if(checkToken()==null){ // 토큰이 없거나 만료되었으면 발급
            val tokencall = tokenapiService.getToken(
                tokenauthorization,
                "application/x-www-form-urlencoded",
            )

            tokencall.enqueue(object : Callback<TokenResponse?> {
                override fun onResponse(
                    tokencall: Call<TokenResponse?>,
                    response: Response<TokenResponse?>
                ) {

                    token= response.body()?.access_token
                    val expiration = System.currentTimeMillis() + 50 * 60 * 1000 // 50분
                    expirationTime = expiration

                    //토큰 발급후 작성
                    val call = emotionapiService.recognizeEmotion(
                        config,
                        "Bearer $token",
                        "$emotionapikey",
                        "COOKIE"
                    )
                    call.enqueue(object : Callback<EmotionRecognitionResponse?> {
                        override fun onResponse(
                            call: Call<EmotionRecognitionResponse?>,
                            response: Response<EmotionRecognitionResponse?>
                        ) {

                            if (response.isSuccessful) {
                                // response 처리 -- text
                                val emotion = response.body()?.results?.uni_modal?.text.toString()
                                tv.setText(emotion)
                                Log.d("TAG","감정인식 api Sucess!")


                            } else {
                                // 에러 처리
                                tv.setText("error : ${response}")
                                Log.d("TAG","감정인식 api 에러")

                            }
                        }

                        override fun onFailure(call: Call<EmotionRecognitionResponse?>, t: Throwable) {
                            // 에러 처리
                            tv.setText("error : ${t}")

                        }
                    })



                }

                override fun onFailure(call: Call<TokenResponse?>, t: Throwable) {
                    Log.d("TAG","토큰 발급 실패------ : $t")


                    // 에러 처리
                }
            })

        }else{ //토큰이 만료x

            //토큰
            val call = emotionapiService.recognizeEmotion(
                config,
                "Bearer $token",
                "$emotionapikey",
                "COOKIE"
            )
            call.enqueue(object : Callback<EmotionRecognitionResponse?> {
                override fun onResponse(
                    call: Call<EmotionRecognitionResponse?>,
                    response: Response<EmotionRecognitionResponse?>
                ) {
                    if (response.isSuccessful) {
                        // response 처리
                        val emotion = response.body()?.results?.uni_modal?.text?.result
                        tv.setText(emotion)
                        Log.d("TAG","감정인식 api Sucess!")

                    } else {
                        // 에러 처리
                        tv.setText("error : ${response}")
                        Log.d("TAG","감정인식 api 실패(토큰 발급 x)")


                    }
                }

                override fun onFailure(call: Call<EmotionRecognitionResponse?>, t: Throwable) {
                    // 에러 처리
                    tv.setText("error : ${t}")

                }
            })

        }




    }

}


