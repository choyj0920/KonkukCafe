package com.kounkukcafe.kounkukcafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.kounkukcafe.kounkukcafe.databinding.ActivityVoiceRecordingBinding
import java.util.*

class VoiceRecordingActivity : AppCompatActivity() {
    lateinit var binding: ActivityVoiceRecordingBinding
    lateinit var tts: TextToSpeech
    var isTtsReady = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceRecordingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTTs()
        init()

    }
    private fun init(){



        binding.voiceIcon.setOnClickListener {
            if (isTtsReady)
                tts.speak(binding.askingTextView.text.toString(), TextToSpeech.QUEUE_ADD, null, null)
        }


    }

    private fun initTTs() {
        tts = TextToSpeech(this) {status->
            if(status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                tts.language = Locale.KOREA
                tts.speak(binding.askingTextView.text.toString(), TextToSpeech.QUEUE_ADD, null, null)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        tts.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }


}