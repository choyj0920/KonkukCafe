package com.kounkukcafe.kounkukcafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import com.kounkukcafe.kounkukcafe.apiutil.ApiManager
import com.kounkukcafe.kounkukcafe.databinding.ActivityVoiceRecordingBinding
import java.util.*

class VoiceRecordingActivity : AppCompatActivity() {
    lateinit var binding: ActivityVoiceRecordingBinding
    lateinit var mRecognizer: SpeechRecognizer
    lateinit var tts: TextToSpeech
    var question_text = arrayOf("오늘 하루는 어떠셨나요?","휴일에 하고싶은 일이 있나요?","먹고싶은 음식이 있나요?" )
    var isTtsReady = false
    var isSttReady = false
    var emotion = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVoiceRecordingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initTTs()
        initStt()

    }

    fun goNext(){

        val nextIntent = Intent(this, VoiceLoadingActivity::class.java)
        nextIntent.putExtra("emotion",emotion.trim())
        startActivity(nextIntent)

    }

    fun initStt(){
        Log.i("init Stt", "init stt")
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mRecognizer.setRecognitionListener(listener)
        isSttReady = true
        startStt()

    }
    fun startStt(){

        if(isSttReady){
            Log.e("Stt", "stt ready2")
            intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            }
            Log.i("Stt", "stt ok")
            mRecognizer.startListening(intent)
        }

    }
    private fun init(){
        select_question()

        binding.apply {
            voiceIcon.setOnClickListener {
                if (isTtsReady)
                    tts.speak(
                        binding.askingTextView.text.toString(),
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
            }
            questionRetryButton.setOnClickListener {
                select_question()
                voiceTextView.text = "인식 중"
                questionRetryButton.visibility = View.GONE
                startStt()
            }

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

    private fun select_question(){
        val random = (0..2).random()

        binding.askingTextView.text = question_text[random]

    }

    override fun onStop() {
        super.onStop()
        tts.stop()

    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            isSttReady = true
            Toast.makeText(applicationContext, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show()
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            val message: String = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러 발생"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러 발생"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한이 없습니다"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러 발생"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 오류"
                SpeechRecognizer.ERROR_SERVER -> "서버 오류"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            binding.voiceTextView.text = "인식 실패"
            binding.questionRetryButton.visibility = View.VISIBLE
        }

        override fun onResults(results: Bundle?) {
            //
            val matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

            for (i in matches.indices) {
                emotion = matches[i]
            }
            binding.voiceRecordedText.text = emotion

            val inputtext:String=emotion.trim().replace("\n","")
            if(inputtext.length != 0){
                ApiManager.callEmotionrecognitionText(inputtext,binding.voiceRecordedText)
            }

            binding.voiceTextView.text = "인식 완료"
            binding.questionRetryButton.visibility = View.VISIBLE
            goNext()
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }


}