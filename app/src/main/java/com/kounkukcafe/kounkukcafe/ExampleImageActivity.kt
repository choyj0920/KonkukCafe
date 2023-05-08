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
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, stream)

        var filepath= getExternalFilesDir(null).toString() +"/imageRecognition"
        val dir=File(filepath)
        if(!dir.exists())
            dir.mkdirs()

        val fileName="temp.png"
        var file =File(dir,fileName)
        filepath=file.absolutePath

        file.writeBitmap(bitmap,Bitmap.CompressFormat.PNG,50)
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