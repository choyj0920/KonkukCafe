package com.kounkukcafe.kounkukcafe


import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.kounkukcafe.kounkukcafe.apiutil.ApiManager
import com.kounkukcafe.kounkukcafe.databinding.ActivityVoiceBinding

class VoiceActivity : AppCompatActivity() {
    private lateinit var intent: Intent

    private lateinit var binding: ActivityVoiceBinding
    private val PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, arrayOf(
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.RECORD_AUDIO), PERMISSION)
        }

        binding.buttonToNext.setOnClickListener {
            val nextIntent = Intent(this, VoiceRecordingActivity::class.java)
            startActivity(nextIntent)
        }
    }




}