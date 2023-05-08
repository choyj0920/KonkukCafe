package com.kounkukcafe.kounkukcafe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.kounkukcafe.kounkukcafe.apiutil.ApiManager
import com.kounkukcafe.kounkukcafe.databinding.ActivityExampleImageBinding
import java.io.File
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class ExampleImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExampleImageBinding
    var selectedimagefile :File? =null

    companion object {
        private const val REQUEST_IMAGE_PICK = 1000
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example_image)
        binding = ActivityExampleImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        binding.ivInput.setOnClickListener {

            startActivityForResult(pickImageIntent, REQUEST_IMAGE_PICK)

        }

        binding.button.setOnClickListener {
            ApiManager.callEmotionrecognitionImage(ivToFile(binding.ivInput),binding.resultText)

        }

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

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val imageUri = data?.data
            // uri 객체를 이용하여 파일 경로 생성
            val filePath = getPathFromUri(imageUri)

            // 파일 객체 생성
            selectedimagefile = File(filePath)
            binding.ivInput.setImageURI(imageUri)
        }
    }
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