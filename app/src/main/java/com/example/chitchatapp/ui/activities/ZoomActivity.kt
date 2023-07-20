package com.example.chitchatapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.chitchatapp.R
import com.example.chitchatapp.constants.Constants
import com.example.chitchatapp.databinding.ActivityZoomBinding

class ZoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityZoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zoom)

        val imageUrl = intent.getStringExtra(Constants.ZOOM_IMAGE_URL)
        Glide.with(this)
            .load(imageUrl)
            .into(binding.zoomableIv)
    }
}