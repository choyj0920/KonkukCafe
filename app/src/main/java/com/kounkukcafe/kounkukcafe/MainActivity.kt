package com.kounkukcafe.kounkukcafe

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.kounkukcafe.kounkukcafe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val nextIntent = Intent(this, VoiceActivity::class.java)
            startActivity(nextIntent)
        }
        binding.button2.setOnClickListener {
            val nextIntent = Intent(this, FaceActivity::class.java)
            startActivity(nextIntent)
        }
        binding.button3.setOnClickListener {
            val nextIntent = Intent(this, CafeListActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnExample.setOnClickListener {
            val nextIntent = Intent(this, ExampleActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnExampleImage.setOnClickListener {
            val nextIntent = Intent(this, ExampleImageActivity::class.java)
            startActivity(nextIntent)
        }




    }

}