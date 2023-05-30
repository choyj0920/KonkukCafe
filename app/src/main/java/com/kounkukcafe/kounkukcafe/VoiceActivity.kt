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
    private lateinit var mRecognizer: SpeechRecognizer
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



        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        binding.button.setOnClickListener {


            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            mRecognizer.setRecognitionListener(listener)
            mRecognizer.startListening(intent)
        }


//        binding.button.setOnClickListener {
//            val inputtext:String=binding.textView.text.toString().trim().replace("\n","")
//            if(inputtext.length != 0){
//                ApiManager.callEmotionrecognitionText(inputtext,binding.resultText)
//            }
//        }
        binding.buttontolist.setOnClickListener {
            val nextIntent = Intent(this, CafeListActivity::class.java)
            nextIntent.putExtra("emotion",binding.resultText.text.toString().trim())
            startActivity(nextIntent)
        }
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
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
            Toast.makeText(applicationContext, "에러가 발생하였습니다. : $message", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle?) {
            //
            val matches: ArrayList<String> = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

            for (i in matches.indices) {
                binding.textView.text = matches[i]
            }

            val inputtext:String=binding.textView.text.toString().trim().replace("\n","")
            if(inputtext.length != 0){
                ApiManager.callEmotionrecognitionText(inputtext,binding.resultText)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }


}