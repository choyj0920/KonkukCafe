package com.kounkukcafe.kounkukcafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kounkukcafe.kounkukcafe.apiutil.ApiManager
import com.kounkukcafe.kounkukcafe.databinding.ActivityExampleBinding
import com.kounkukcafe.kounkukcafe.databinding.ActivityMainBinding

class ExampleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        binding = ActivityExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val inputtext:String=binding.inputText.text.toString();
            if(inputtext.length != 0){
                ApiManager.callEmotionrecognition(inputtext,binding.resultText);
            }
        }

    }
}