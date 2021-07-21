package com.razortm.altynkot

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatActivity
import com.razortm.altynkot.databinding.ActivityResultBinding


class ResultActivity : AppCompatActivity() {


    private lateinit var binding: ActivityResultBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()

        binding.imageFilteredImage.setImageURI(intent.data)


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.shareBtn.setOnClickListener {
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            with(Intent(Intent.ACTION_SEND)){
                putExtra(Intent.EXTRA_STREAM, intent.data)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/*"
                startActivity(this)
            }
        }

        binding.homeBtn.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }
}