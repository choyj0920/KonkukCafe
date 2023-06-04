package com.kounkukcafe.kounkukcafe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.kounkukcafe.kounkukcafe.apiutil.ApiManager
import java.io.File
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.kounkukcafe.kounkukcafe.apiutil.simpleEmotion
import com.kounkukcafe.kounkukcafe.databinding.ActivityFaceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class FaceActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: ActivityFaceBinding
    //var selectedimagefile :File? =null
    var selectedimagefile :File? =null

    companion object {
        private const val REQUEST_IMAGE_PICK = 1000
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)
        binding = ActivityFaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPhoto.setOnClickListener(this)
        binding.ivInput.setImageBitmap(intent.getParcelableExtra("photo"))

        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        binding.ivInput.setOnClickListener {

            startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK)

        }

        binding.submit.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) { // 비동기 형태라 외부 쓰레드에서 실행해야함
                var emotion = ApiManager.callErFromImage(ivToFile(binding.ivInput))
                if (emotion!=null){
                    goNext(emotion)
                }
                // 가장최근에 한 감정인식시 해당 감정 정보가 여기에 저장되어서 어디에서도 사용
                ApiManager.curSimpleEmotion


            }

//            ApiManager.callEmotionrecognitionImage(ivToFile(binding.ivInput),binding.resultText)
//            val next=Intent(this,VoiceLoadingActivity::class.java)
//            next.putExtra("result",binding.resultText.text)
//            startActivity(next)

//            val result=ApiManager.callEmotionrecognitionImage(ivToFile(binding.ivInput),binding.resultText)
//            val next=Intent(this,VoiceLoadingActivity::class.java)
//            next.putExtra("result",result)
//            startActivity(next)
        }
    }
    fun goNext(emotion: simpleEmotion) {

        val next=Intent(this,VoiceLoadingActivity::class.java)
        next.putExtra("emotion",emotion.result)
        startActivity(next)

    }

    private fun ivToFile(image: ImageView): File {
        var bitmap = (image.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)


        // 크기 너무 크게하면 api에서 안받아줌  크기 줄이기 최대 400으로
        val width = bitmap.width
        val height = bitmap.height
        val maxSide = if (width > height) width else height // 가로, 세로 중 큰 쪽 찾기
        val scale = 400f / maxSide // 큰 쪽이 400이 되도록 비율 계산
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        val newbitmap =Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        var filepath= getExternalFilesDir(null).toString() +"/imageRecognition"
        val dir=File(filepath)
        if(!dir.exists())
            dir.mkdirs()

        val fileName="temp.png"
        var file =File(dir,fileName)
        filepath=file.absolutePath

        file.writeBitmap(newbitmap,Bitmap.CompressFormat.PNG,50)
        //var file = File(filepath+"/"+fileName)
        file=File(filepath)
        return file
    }
    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
            out.close()
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
            binding.ivInput.setImageBitmap(imageBitmap)
        }else if(requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK){
            val imageUri = data?.data
            // uri 객체를 이용하여 파일 경로 생성
            val filePath = getPathFromUri(imageUri)


            // 파일 객체 생성
            selectedimagefile = File(filePath)
            binding.ivInput.setImageURI(imageUri)

        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            // 카메라촬영 클릭 이벤트
            R.id.btnPhoto -> {
                // 카메라 기능을 Intent
                val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(i, 0)
            }
        }
    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
//            val imageUri = data?.data
//            // uri 객체를 이용하여 파일 경로 생성
//            val filePath = getPathFromUri(imageUri)
//
//            // 파일 객체 생성
//            selectedimagefile = File(filePath)
//            binding.ivInput.setImageURI(imageUri)
//        }
//    }

    private fun getPathFromUri(uri: Uri?): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null)
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val path = cursor.getString(columnIndex)
        cursor.close()
        return path
    }
}