package com.kounkukcafe.kounkukcafe

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.kounkukcafe.kounkukcafe.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val information = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val signatures = information.signingInfo.apkContentsSigners
            for (signature in signatures) {
                val md = MessageDigest.getInstance("SHA").apply {
                    update(signature.toByteArray())
                }
                val HASH_CODE = String(Base64.encode(md.digest(), 0))

                Log.d("TAG", "HASH_CODE -> $HASH_CODE")
            }
        } catch (e: Exception) {
            Log.d("TAG", "Exception -> $e")
        }
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