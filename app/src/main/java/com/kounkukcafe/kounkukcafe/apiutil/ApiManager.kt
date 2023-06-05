package com.kounkukcafe.kounkukcafe.apiutil

import android.annotation.SuppressLint
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

interface EmotionApiService {
    @Multipart
    @POST("emotion/er/v1/recognition")
    fun recognizeEmotionText(
        @Part("config") config: RequestBody,
        @Header("Authorization") auth: String,
        @Header("x-api-key") apiKey: String,
        @Header("Cookie") cookie: String
    ): Call<ERResponse?>

    @Multipart
    @POST("emotion/er/v1/recognition")
    suspend fun recognizeEmotionText_v2(
        @Part("config") config: RequestBody,
        @Header("Authorization") auth: String,
        @Header("x-api-key") apiKey: String,
        @Header("Cookie") cookie: String
    ): Response<ERResponse?>

    @Multipart
    @POST("emotion/er/v1/recognition")
    fun recognizeEmotionImage(
        @Part("config") config: RequestBody,
        @Part image: MultipartBody.Part,
        @Header("Authorization") auth: String,
        @Header("x-api-key") apiKey: String,
        @Header("Cookie") cookie: String
    ): Call<ERResponse?>

    @Multipart
    @POST("emotion/er/v1/recognition")
    suspend fun recognizeEmotionImage_v2(
        @Part("config") config: RequestBody,
        @Part image: MultipartBody.Part,
        @Header("Authorization") auth: String,
        @Header("x-api-key") apiKey: String,
        @Header("Cookie") cookie: String
    ): Response<ERResponse?>
}
interface TokenApiService {
    @POST("v1/cognito")
    fun getToken(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") content_Type: String): Call<TokenResponse?>

    @POST("v1/cognito")
    suspend fun getToken_v2(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") content_Type: String): Response<TokenResponse?>
}

// 해당 클래스는 싱글톤패턴 클래스로 getinstance로 가져와야함
object ApiManager {


    // 토큰 발급용 Authorization - 토큰 발급에만 쓰이는 Clienid/password 인코딩한거
    val tokenauthorization ="Basic N2wyYjNmbG5zdnUwMWVsNGg3N3M2OTBqdTY6MWMxa3VvNWF1bDB0M2ZqcTZqdTZlc3A0ZGg5bjlsaGE5a3N0YzduNmwyY2VvYW1rZW12cA=="
    // 토큰 발급용 baseURl
    private val tokenApiBaseUrl = "https://oauth.api.lgthinqai.net:443/"
    // 감정인식용 baseURL
    private val emotionApiBaseUrl = "https://korea.api.lgthinqai.net:443/"
    // 감정인식 api-key
    val emotionapikey="MTtlZjUyYzFmYTViMjc0NGNkYTg3NDE3NGYwOWU5NGQ0YTsxNjgxMDk2OTAzMzk1"

    var curSimpleEmotion:simpleEmotion? =null


    var token: String? = null
    var expirationTime: Long = 0

    private val tokenclient = OkHttpClient.Builder().build()
    var tokenapiService: TokenApiService

    // emotion recog 관련
    private val emotionclient = OkHttpClient.Builder().build()
    var emotionapiService: EmotionApiService

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
    private fun checkToken(): String? {
        if (token !=null && System.currentTimeMillis() < expirationTime) {
            Log.d("TAG","checkToken 기존사용")
            return token
        }
        Log.d("TAG","checkToken 만료 or 발급필요")

        return null
    }


    // 토큰 갱신 -만들어는 뒀는데 감정인식에 이미 작성되어있어서 쓸모없음
    suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        if (checkToken() != null) {
            return@withContext token
        }

        try {

            val response = tokenapiService.getToken_v2(
                tokenauthorization,
                "application/x-www-form-urlencoded",
            )

            if (response.isSuccessful) {
                token = response.body()?.access_token
                val expiration = System.currentTimeMillis() + 50 * 60 * 1000 // 50분
                expirationTime = expiration

                token

            } else {

                null
            }
        } catch (e: Exception) {
            // 에러 처리

            null
        }


    }

    // 감정인식 inputtext넣고 , 텍스트뷰 넣으면,감정인식 결과 그 텍스트뷰에 글 뿌려줌
    fun callEmotionrecognitionText(inputText: String, tv:TextView) {
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
                    val call = emotionapiService.recognizeEmotionText(
                        config,
                        "Bearer $token",
                        emotionapikey,
                        "COOKIE"
                    )
                    call.enqueue(object : Callback<ERResponse?> {
                        @SuppressLint("SetTextI18n")
                        override fun onResponse(
                            call: Call<ERResponse?>,
                            response: Response<ERResponse?>
                        ) {

                            if (response.isSuccessful) {
                                // response 처리 -- text

                                val emotion = response.body()?.results?.uni_modal?.text?.result
                                if(emotion!=null){
                                    Log.d("TAG","감정인식 api Sucess!")
                                    tv.setText(emotion.toString())
                                }else{
                                    Log.d("TAG","감정인식 api fail!")
                                    val json = JSONObject(response.body().toString()) // toString() is not the response body, it is a debug representation of the response body
                                    val resultCode = json.getString("17003")
                                    val message=json.getString("message")
                                    tv.text = "$resultCode  : $message"
                                }

                            } else {
                                // 에러 처리
                                tv.text = "error : ${response}"
                                Log.d("TAG","감정인식 api 에러")

                            }
                        }

                        override fun onFailure(call: Call<ERResponse?>, t: Throwable) {
                            // 에러 처리
                            tv.text = "error : ${t}"

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
            val call = emotionapiService.recognizeEmotionText(
                config,
                "Bearer $token",
                emotionapikey,
                "COOKIE"
            )
            call.enqueue(object : Callback<ERResponse?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ERResponse?>,
                    response: Response<ERResponse?>
                ) {
                    if (response.isSuccessful) {
                        // response 처리
                        val emotion = response.body()?.results?.uni_modal?.text?.result
                        if(emotion!=null){
                            Log.d("TAG","감정인식 api Sucess!")
                            tv.setText(emotion.toString())

                        }else{
                            Log.d("TAG","감정인식 api fail!")
                            val json = JSONObject(response.body().toString()) // toString() is not the response body, it is a debug representation of the response body
                            val resultCode = json.getString("17003")
                            val message=json.getString("message")
                            tv.text = "$resultCode  : $message"
                        }

                    } else {
                        // 에러 처리
                        tv.text = "error : ${response}"
                        Log.d("TAG","감정인식 api 실패(토큰 발급 x)")


                    }
                }

                override fun onFailure(call: Call<ERResponse?>, t: Throwable) {
                    // 에러 처리
                    tv.text = "error : ${t}"

                }
            })

        }




    }

    suspend fun callErFromText(inputText: String) :simpleEmotion? = withContext(Dispatchers.IO) {

        if(getToken() ==null){
            Log.d("TAG","에러로 토큰발급 실패 ")
            return@withContext null
        }

        // emotion api 작성


        try {
            val config = "{\"type\": \"EMOTION_RECOGNITION\",\"input\": {\"type\": \"TEXT\", \"text\": \"${inputText}\" }}".toRequestBody("text/plain".toMediaType())
            val response = emotionapiService.recognizeEmotionText_v2(
                config,
                "Bearer $token",
                emotionapikey,
                "COOKIE"
            )

            if (response.isSuccessful) {
                Log.d("TAG","정상 실행 : ${simpleEmotion(response.body()?.results?.uni_modal?.text!!)}")
                this@ApiManager.curSimpleEmotion=simpleEmotion(response.body()?.results?.uni_modal?.text!!)
                this@ApiManager.curSimpleEmotion
            } else {
                // 에러 처리
                this@ApiManager.curSimpleEmotion=null
                null
            }
        } catch (e: Exception) {
            // 네트워크 에러 처리
            this@ApiManager.curSimpleEmotion=null

            null
        }
    }
    suspend fun callErFromImage(imageFile: File) :simpleEmotion? = withContext(Dispatchers.IO) {

        if(getToken() ==null){
            Log.d("TAG","에러로 토큰발급 실패 ")
            return@withContext null
        }

        // emotion api 작성


        try {
            val image = MultipartBody.Part.createFormData(
                "image[]",
                imageFile.name,
                imageFile.asRequestBody("image/jpeg".toMediaType())
            )
            // emotion api 작성
            imageFile.name
            val config =
                "{\"type\": \"EMOTION_RECOGNITION\",\"input\": {\"type\": \"IMAGE\",\"imageConfigs\": [{\"id\": \"${imageFile.nameWithoutExtension}\",\"format\": \"jpg\"}   ]  },   \"additionalConfig\": {}}".trimIndent()
                    .toRequestBody("text/plain".toMediaType())


            val response = emotionapiService.recognizeEmotionImage_v2(
                config,
                image,
                "Bearer $token",
                "$emotionapikey",
                "COOKIE"
            )

            if (response.isSuccessful) {
                this@ApiManager.curSimpleEmotion=simpleEmotion(response.body()?.results?.uni_modal?.image!!)
                Log.d("TAG","감정인식(이미지) 성공${this@ApiManager.curSimpleEmotion.toString()}")

                this@ApiManager.curSimpleEmotion
            } else {
                // 에러 처리
                this@ApiManager.curSimpleEmotion=null
                null
            }
        } catch (e: Exception) {
            // 네트워크 에러 처리
            Log.d("TAG",e.toString())
            this@ApiManager.curSimpleEmotion=null

            null
        }
    }



    fun callEmotionrecognitionImage(imageFile: File, tv:TextView) {

        val image = MultipartBody.Part.createFormData(
            "image[]",
            imageFile.name,
            imageFile.asRequestBody("image/jpeg".toMediaType())
        )
        // emotion api 작성
        imageFile.name
        val config =
            "{\"type\": \"EMOTION_RECOGNITION\",\"input\": {\"type\": \"IMAGE\",\"imageConfigs\": [{\"id\": \"${imageFile.nameWithoutExtension}\",\"format\": \"jpg\"}   ]  },   \"additionalConfig\": {}}".trimIndent()
                .toRequestBody("text/plain".toMediaType())

        if (checkToken() == null) { // 토큰이 없거나 만료되었으면 발급
            val tokencall = tokenapiService.getToken(
                tokenauthorization,
                "application/x-www-form-urlencoded",
            )

            tokencall.enqueue(object : Callback<TokenResponse?> {
                override fun onResponse(
                    tokencall: Call<TokenResponse?>,
                    response: Response<TokenResponse?>
                ) {

                    token = response.body()?.access_token
                    val expiration = System.currentTimeMillis() + 50 * 60 * 1000 // 50분
                    expirationTime = expiration

                    //토큰 발급후 작성
                    val call = emotionapiService.recognizeEmotionImage(
                        config,
                        image,
                        "Bearer $token",
                        "$emotionapikey",
                        "COOKIE"
                    )
                    call.enqueue(object : Callback<ERResponse?> {
                        override fun onResponse(
                            call: Call<ERResponse?>,
                            response: Response<ERResponse?>
                        ) {

                            if (response.isSuccessful) {
                                // response 처리 -- text

                                val emotion = response.body()?.results?.uni_modal?.image?.result
                                if (emotion != null) {
                                    tv.setText(emotion.toString())

                                } else {
                                    tv.setText("error : ${response}")
                                }


                            } else {
                                // 에러 처리
                                tv.setText("error : ${response}")

                            }
                        }

                        override fun onFailure(call: Call<ERResponse?>, t: Throwable) {
                            // 에러 처리
                            tv.setText("error : ${t}")
                        }
                    })


                }

                override fun onFailure(call: Call<TokenResponse?>, t: Throwable) {
                    Log.d("TAG", "토큰 발급 실패------ : $t")

                    // 에러 처리
                }
            })

        } else { //토큰이 만료x

            //토큰
            val call = emotionapiService.recognizeEmotionImage(
                config,
                image,
                "Bearer $token",
                "$emotionapikey",
                "COOKIE"
            )
            call.enqueue(object : Callback<ERResponse?> {
                override fun onResponse(
                    call: Call<ERResponse?>,
                    response: Response<ERResponse?>
                ) {
                    if (response.isSuccessful) {
                        // response 처리
                        val emotion = response.body()?.results?.uni_modal?.image?.result
                        if (emotion != null) {
                            Log.d("TAG", "감정인식 api Sucess!")
                            tv.setText(emotion.toString())
                        } else {
                            Log.d("TAG", "감정인식 api fail!")
                            tv.setText("error : ${response}")
                        }

                    } else {
                        // 에러 처리
                        tv.setText("error : ${response}")
                        Log.d("TAG", "감정인식 api 실패(토큰 발급 x)")

                    }
                }

                override fun onFailure(call: Call<ERResponse?>, t: Throwable) {
                    // 에러 처리
                    tv.setText("error : ${t}")
                }
            })

        }
    }

}


