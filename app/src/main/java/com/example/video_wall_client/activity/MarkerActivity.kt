package com.example.video_wall_client.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.video_wall_client.data.GlobalState
import com.example.video_wall_client.databinding.ActivityMarkerBinding

class MarkerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMarkerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkerBinding.inflate(layoutInflater) // xml 레이아웃을 실제 View 객체로 생성, 코드에서 안전하게 접근할 수 있는 binding 객체로 만들어줌
        setContentView(binding.root)                            // 생성한 View 트리(binding.root)를 Activity의 화면 내용으로 시스템에 등록

        hideSystemUI()  // 상단 UI 바 숨기기

        val rawData = GlobalState.bitmap    // GlobalState의 bitmap 데이터를 가져오기

        // bitmap 데이터가 존재할 경우 이미지화시켜 그리기
        if (rawData != null) {
            val originBitmap = createBitmapFromMatrix(rawData)
            binding.imgMarker.setImageBitmap(originBitmap)
        }
    }

    // 상단 UI 바 숨기는 함수
    private fun hideSystemUI() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    // 2차원 배열을 가지고 ArUco 마커 이미지를 생성하는 함수
    private fun createBitmapFromMatrix(matrix: List<List<Int>>): Bitmap {
        val height = matrix.size
        val width = matrix[0].size
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val value = matrix[y][x]

                val color = if (value == 0) Color.BLACK else Color.WHITE

                bitmap.setPixel(x, y, color)
            }
        }
        return Bitmap.createScaledBitmap(bitmap, 1000, 1000, false)
    }
}