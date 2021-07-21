package com.razortm.altynkot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.razortm.altynkot.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val IMAGE_REQUEST_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val RESULT_CODE = 3
        private const val PERMIS_CODE = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this){}

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        supportActionBar!!.hide()

        binding.editButton.setOnClickListener {

            if(SDK_INT >= Build.VERSION_CODES.Q) {

            Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).also { pickerIntent ->
                pickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(pickerIntent, IMAGE_REQUEST_CODE)
            } } else {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMIS_CODE
                    )

                } else if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.type = "image/*"
                    startActivityForResult(intent, IMAGE_REQUEST_CODE)
                }
            }
        }

        binding.cameraButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    32)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data!!.data != null) {

                val filePath = data.data

                val dsPhotoEditorIntent = Intent(this, DsPhotoEditorActivity::class.java)
                dsPhotoEditorIntent.data = filePath
                dsPhotoEditorIntent.putExtra(
                    DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
                    "Images")

                val toolsToHide = intArrayOf(DsPhotoEditorActivity.TOOL_ORIENTATION)
                dsPhotoEditorIntent.putExtra(
                    DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
                    toolsToHide)
                startActivityForResult(dsPhotoEditorIntent, RESULT_CODE)
            }
        }
            if (requestCode == RESULT_CODE) {
            val intent = Intent(this, ResultActivity::class.java)
                intent.data = data!!.data
            startActivity(intent)
        }
            if (resultCode == CAMERA_REQUEST_CODE) {
            val photo = data!!.extras!!.get("data") as Bitmap
            val uri = getImageUri(photo)
            val dsPhotoEditorIntent = Intent(this, DsPhotoEditorActivity::class.java)
            dsPhotoEditorIntent.data = uri
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,
                "Images")
            val toolsToHide = intArrayOf(DsPhotoEditorActivity.TOOL_ORIENTATION)
            dsPhotoEditorIntent.putExtra(
                DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,
                toolsToHide)
            startActivityForResult(dsPhotoEditorIntent, RESULT_CODE)
        }
    }


    private fun getImageUri(bitmap: Bitmap?): Uri {
        val arrayOutputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, arrayOutputStream)
        val path = MediaStore.Images.Media.insertImage(contentResolver,
                bitmap, "Title", "Description")
        return Uri.parse(path)
    }
}
