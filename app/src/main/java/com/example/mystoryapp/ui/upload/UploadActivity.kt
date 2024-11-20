package com.example.mystoryapp.ui.upload

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.R
import com.example.mystoryapp.data.Injection
import com.example.mystoryapp.databinding.ActivityUploadBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private lateinit var photoURI: Uri
    private var imageUri: Uri? = null
    private lateinit var viewModel: UploadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelFactory = Injection.provideUploadViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory)[UploadViewModel::class.java]

        binding.cameraButton.setOnClickListener {
            openCamera()
        }

        binding.galleryButton.setOnClickListener {
            openGallery()
        }

        binding.btnPostStory.setOnClickListener {
            uploadStory()
        }

        // Observe upload result
        viewModel.uploadResult.observe(this) { result ->
            result.onSuccess { response ->
                if (!response.error!!) {
                    setResult(Activity.RESULT_OK)
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }
            }.onFailure { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        }

        // Restore the image URI if it exists
        if (savedInstanceState != null) {
            imageUri = savedInstanceState.getParcelable("imageUri")
            imageUri?.let {
                compressAndSetImage(it)
            }
        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create a file to save the image
                val photoFile: File? = createImageFile()
                photoFile?.also {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_$timeStamp", /* prefix */
            ".jpg",             /* suffix */
            storageDir          /* directory */
        )
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    compressAndSetImage(photoURI)
                }
                GALLERY_REQUEST_CODE -> {
                    imageUri = data?.data
                    imageUri?.let {
                        compressAndSetImage(it)
                    }
                }
            }
        }
    }

    private fun compressAndSetImage(uri: Uri) {
        val bitmap = decodeSampledBitmapFromUri(uri, 1024, 1024) // Set width and height limit
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            var quality = 100

            // Compress the image until it's less than 1MB
            do {
                outputStream.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                quality -= 5
            } while (outputStream.size() / 1024 > 1024 && quality > 0)

            val compressedBitmap = BitmapFactory.decodeStream(ByteArrayInputStream(outputStream.toByteArray()))
            binding.imageStoryUpload.setImageBitmap(compressedBitmap)
        } else {
            Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun decodeSampledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun uploadStory() {
        // Validasi input
        val description = binding.tvDes.text.toString().trim()
        if (description.isEmpty()) {
            binding.tvDes.error = "Deskripsi harus diisi"
            return
        }

        // Pastikan gambar sudah dipilih
        val drawable = binding.imageStoryUpload.drawable
        if (drawable == null) {
            Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Konversi ImageView ke File
        binding.imageStoryUpload.isDrawingCacheEnabled = true
        binding.imageStoryUpload.buildDrawingCache()
        val bitmap = binding.imageStoryUpload.drawingCache

        // Simpan bitmap ke file
        val file = bitmapToFile(bitmap)

        // Cek ukuran file
        if (file.length() > 1 * 1024 * 1024) { // 1MB
            Toast.makeText(this, "Ukuran file maksimal 1MB", Toast.LENGTH_SHORT).show()
            return
        }

        // Persiapkan data untuk upload
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val photoPart = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            photoPart
        )

        // Panggil fungsi upload di ViewModel
        viewModel.uploadStory(descriptionPart, imageMultipart)
    }

    // Tambahkan fungsi bantuan untuk convert bitmap ke file
    private fun bitmapToFile(bitmap: Bitmap): File {
        // Buat file sementara
        val file = File(cacheDir, "uploaded_image_${System.currentTimeMillis()}.jpg")

        try {
            // Buka output stream
            val outputStream = FileOutputStream(file)

            // Kompresi bitmap ke file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            // Tutup stream
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("imageUri", imageUri)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, System.currentTimeMillis().toString())

        try {
            val inputStream = contentResolver.openInputStream(selectedImg)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }
}