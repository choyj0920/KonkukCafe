package com.kounkukcafe.kounkukcafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kounkukcafe.kounkukcafe.databinding.ActivityAgainBinding
import com.kounkukcafe.kounkukcafe.databinding.ActivityRecommendBinding

class RecommendActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecommendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRecommendBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initImage()
        initLayout()
    }
    private fun initImage(){
        val result=intent.getStringExtra("result")!!
        var amp=0
        var offset=0
        when(result){
            "행복" ->{//행복인 경우
                amp=R.drawable.rm_happy
                offset=R.drawable.rm_sad
            }
            "슬픔"->{//슬픔인 경우
                amp=R.drawable.rm_sad
                offset=R.drawable.rm_happy
            }
            "분노"->{//분노인 경우
                amp=R.drawable.rm_angry
                offset=R.drawable.rm_happy
            }
        }
        binding.amplification.setImageResource(amp)
        binding.offset.setImageResource(offset)
    }

    private fun initLayout() {
        //RecommendActivity로 넘어올때 감정결과 분석에 따라서 binding.amplification, binding.offset 이미지 바꿔줘야함
        binding.amplification.setOnClickListener{
            val next= Intent(this,CafeListActivity::class.java)  //증폭 카페리스트 추천으로 수정해야함
            next.putExtra("isamplification",true)
            startActivity(next)
        }
        binding.offset.setOnClickListener {
            val next= Intent(this,CafeListActivity::class.java) //상쇄 카페리스트로 수정해야함
            next.putExtra("isamplification",false)
            startActivity(next)
        }
        binding.home.setOnClickListener {
            val next= Intent(this,MainActivity::class.java)
            startActivity(next)
        }

    }
}