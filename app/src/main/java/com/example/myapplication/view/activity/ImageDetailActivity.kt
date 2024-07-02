// ImageDetailActivity.kt
package com.example.myapplication.view.activity

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R

class ImageDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        val imageView: ImageView = findViewById(R.id.imageViewDetail)
        val imageUri: Uri? = intent.getParcelableExtra("image_uri")

        imageUri?.let {
            Glide.with(this).load(it).into(imageView)
        }

        imageView.setOnClickListener {
            finish() // 이미지를 클릭하면 액티비티를 종료하여 원래 화면으로 돌아감
        }
    }
}
