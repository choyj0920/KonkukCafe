package com.kounkukcafe.kounkukcafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kounkukcafe.kounkukcafe.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    lateinit var binding:ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
        initButton()
    }

    private fun initLayout() {
        val result=intent.getStringExtra("result")
        var img=0
        when(result){
            "행복" ->{//행복인 경우
                img=R.drawable.happy
            }
            "슬픔"->{//슬픔인 경우
                img=R.drawable.sad
            }
            "분노"->{//분노인 경우
                img=R.drawable.angry
            }
        }
        binding.emotionIcon.setImageResource(img)
        binding.emotionText.setText(result)
    }

    private fun initButton() {
        binding.resultYes.setOnClickListener {
            val next= Intent(this, RecommendActivity::class.java)
            startActivity(next)
        }
        binding.resultNo.setOnClickListener {
            val next= Intent(this, AgainActivity::class.java)
            startActivity(next)
        }
    }

}