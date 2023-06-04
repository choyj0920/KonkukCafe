package com.kounkukcafe.kounkukcafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.kounkukcafe.kounkukcafe.databinding.ActivityVoiceLoadingBinding

class VoiceLoadingActivity : AppCompatActivity() {
    lateinit var binding:ActivityVoiceLoadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVoiceLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val result=intent.getStringExtra("emotion")
        moveResult(2,result)
    }

    private fun moveResult(sec: Int,sent:String?) {
        Handler().postDelayed({
            val intent = Intent(applicationContext, ResultActivity::class.java)
            intent.putExtra("result",sent)
            startActivity(intent)
            finish()
        }, (1000*sec).toLong())
    }
}