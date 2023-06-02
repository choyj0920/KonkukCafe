package com.kounkukcafe.kounkukcafe

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.kounkukcafe.kounkukcafe.databinding.ActivityAgainBinding
import com.kounkukcafe.kounkukcafe.databinding.ActivityResultBinding
import java.io.File

class AgainActivity : AppCompatActivity() {
    lateinit var binding: ActivityAgainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAgainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
    }

    private fun initLayout() {
        binding.camera.setOnClickListener {
            val nextIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(nextIntent, 0)
        }
        binding.recording.setOnClickListener {
            val next= Intent(this, VoiceRecordingActivity::class.java)
            startActivity(next)
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
            val i = Intent(this, FaceActivity::class.java)
            i.putExtra("photo", imageBitmap)
            startActivity(i)
        }
    }


}