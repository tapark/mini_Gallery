package com.example.part2_chap5_mygallery

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity: AppCompatActivity() {

    private val photoList: MutableList<Uri> = mutableListOf()

    private var currentPosition = 0

    private var timer: Timer? = null

    private val photoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.photoImageView)
    }

    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.backgroundPhotoImageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photoframe)

        getPhotoUriFromIntent()
    }

    private fun getPhotoUriFromIntent() {
        val size = intent.getIntExtra("photoListSize", 0)

        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {
                photoList.add(it.toUri())
            }
        }
    }

    private fun startTimer() {
        timer = timer(period = 5 * 1000) {
            runOnUiThread {

                val current = currentPosition
                var next = if (photoList.size <= currentPosition + 1) {
                    0
                } else {
                    currentPosition + 1
                }
                backgroundPhotoImageView.setImageURI(photoList[current])

                photoImageView.alpha = 0f // 투명도 = 투명함
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate().alpha(1.0f).setDuration(1000).start()

                currentPosition = next

            }
        }
    }

    override fun onStop() {
        super.onStop()

        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()

        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
    }




}