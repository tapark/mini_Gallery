package com.example.part2_chap5_mygallery

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.addPhotoButton)
    }

    private val startButton: Button by lazy {
        findViewById<Button>(R.id.startButton)
    }

    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById<ImageView>(R.id.imageView11))
            add(findViewById<ImageView>(R.id.imageView12))
            add(findViewById<ImageView>(R.id.imageView13))
            add(findViewById<ImageView>(R.id.imageView21))
            add(findViewById<ImageView>(R.id.imageView22))
            add(findViewById<ImageView>(R.id.imageView23))
        }
    }

    private val imageUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddButton()
        initStartButton()
    }

    private fun initAddButton() {
        addPhotoButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // todo 권한이 잘 부여되었을때 사진을 선택하는 기능
                    navigatePhotos()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //todo 교육용 팝업 확인 후 권한 팝입을 띄우는 기능
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // todo 권한이 부여됨
                    navigatePhotos()
                }
                else {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                }

            }
            else -> { }
        }
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {

                    if (imageUriList.size == 6) {
                        Toast.makeText(this, "사진은 6개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
                }
                else { }
            }
            else -> { }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 추가하기 위해서 권한이 필요합니다..")
            .setPositiveButton("확인") {_, _ -> requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)}
            .setNegativeButton("취소") {_,_ -> }
            .create()
            .show()
    }

    private fun initStartButton() {
        startButton.setOnClickListener {
            val intent = Intent(this, PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo$index", uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size)
            startActivity(intent)
        }
    }
}