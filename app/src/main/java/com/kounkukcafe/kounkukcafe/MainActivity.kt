package com.kounkukcafe.kounkukcafe

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.kounkukcafe.kounkukcafe.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.P)
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

        binding.btnVoice.setOnClickListener {
            val nextIntent = Intent(this, VoiceActivity::class.java)
            startActivity(nextIntent)
        }
        binding.btnFace.setOnClickListener {
            val nextIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(nextIntent, 0)
        }
        binding.btnCafelist.setOnClickListener {
            val nextIntent = Intent(this, CafeListActivity::class.java)
            startActivity(nextIntent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 카메라 촬영을 하면 이미지뷰에 사진 삽입
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            // Bundle로 데이터를 입력
            val extras: Bundle? = data?.extras

            // Bitmap으로 컨버전
            val imageBitmap = extras?.get("data") as Bitmap

            // 이미지뷰에 Bitmap으로 이미지를 입력
            val i=Intent(this,FaceActivity::class.java)
            i.putExtra("photo",imageBitmap)
            startActivity(i)
        }
    }

}