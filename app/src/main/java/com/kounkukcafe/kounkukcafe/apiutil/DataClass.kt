package com.kounkukcafe.kounkukcafe.apiutil

import com.google.gson.annotations.SerializedName
import kotlin.math.round

data class ERResponse(
    @SerializedName("type")
    val type: String,
    @SerializedName("results")
    val results: EmotionResults
)

data class EmotionResults(

    @SerializedName("resultType")
    val resultType: String,
    @SerializedName("multi_modal")
    val multi_modal: Any?,
    @SerializedName("uni_modal")
    val uni_modal: UniModal
)

data class UniModal(
    @SerializedName("image")
    val image: EmotionText?,
    @SerializedName("audio")
    val audio: EmotionText?,
    @SerializedName("text")
    val text: EmotionText
)

data class EmotionText(
    @SerializedName("result")
    val result: String,
    @SerializedName("probs")
    val probs: List<Double>
){
    override fun toString(): String {
        val count="분노 : ${round(probs[0]*10000)/100}%  싫음 : ${round(probs[1]*10000)/100}%  , 두려움  : ${round(probs[2]*10000)/100}%  ," +
                " 행복 : ${round(probs[3]*10000)/100}%  , 슬픔 : ${round(probs[4]*10000)/100}%  , 놀람 : ${round(probs[5]*10000)/100}%  , 중립 : ${round(probs[6]*10000)/100}%  "
        return "현재 감정 : $result \n${count}"

    }
}

data class TokenResponse(
    @SerializedName("access_token")
    val access_token: String,
    @SerializedName("expires_in")
    val expires_in: Int,
    @SerializedName("token_type")
    val token_type: String
)