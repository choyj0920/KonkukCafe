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
fun getSecondLargestIndex(list: List<Double>): Int {
    return list.mapIndexed { index, value -> Pair(index, value) }
        .sortedByDescending { it.second }
        .getOrNull(1)?.first
        ?: throw RuntimeException("The list must contain at least two elements")
}
data class simpleEmotion(
    var result: String,
    var accuracy:Double
){
    constructor(emotionText: EmotionText):this("1", 0.0){
        when(emotionText.result){
            "분노","싫음","두려움" -> {
                this.result="분노"
                this.accuracy=(emotionText.probs[0]+emotionText.probs[1]+emotionText.probs[2])
            }
            "행복" -> {
                this.result="행복"
                this.accuracy=(emotionText.probs[3])

            }
            "중립" ->{
                var index =getSecondLargestIndex(emotionText.probs)
                var emotion =CafeApiManager.emotionOrder[index]
                when(emotion){
                    "분노","싫음","두려움" -> {
                        this.result="분노"
                        this.accuracy=(emotionText.probs[0]+emotionText.probs[1]+emotionText.probs[2])
                    }
                    "행복" -> {
                        this.result="행복"
                        this.accuracy=(emotionText.probs[3])
                    }

                    "슬픔","놀람" -> {
                        this.result="슬픔"
                        this.accuracy=(emotionText.probs[4]+emotionText.probs[5])
                    }
                }
            }
            "슬픔","놀람" -> {
                this.result="슬픔"
                this.accuracy=(emotionText.probs[4]+emotionText.probs[5])
            }
        }

    }


    override fun toString(): String {
        return "[${result}] ,정확도 : ${accuracy}"
    }

}
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

    companion object {
        fun fromString(str: String): EmotionText {
            val lines = str.split("\n")
            val result = lines[0].substringAfter(": ").trim()
            val probs = lines[1].split(",").map { it.substringAfter(": ").substringBefore("%").toDouble() / 100 }
            return EmotionText(result, probs)
        }
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


data class Cafe(
    @SerializedName("name")
    val name: String,
    @SerializedName("adr")
    val adr: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("rating")
    val rating: String,
    @SerializedName("리뷰감정")
    val 리뷰감정: String,
    @SerializedName("분노")
    val 분노: Double,
    @SerializedName("싫음")
    val 싫음: Double,
    @SerializedName("두려움")
    val 두려움: Double,
    @SerializedName("행복")
    val 행복: Double,
    @SerializedName("슬픔")
    val 슬픔: Double,
    @SerializedName("놀람")
    val 놀람: Double,
    @SerializedName("중립")
    val 중립: Double,
    @SerializedName("lat")
    val lat: String,
    @SerializedName("lng")
    val lng: String
)

data class CafeResponseData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("cafelist")
    val cafelist: List<Cafe>,
    @SerializedName("negativecafelist")
    val negativecafelist: List<Cafe>
)
