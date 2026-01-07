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
        binding = ActivityMarkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        val rawData = GlobalState.bitmap

        if(rawData != null){
            val originBitmap = createBitmapFromMatrix(rawData)

            binding.imgMarker.setImageBitmap(originBitmap)
        }

    }

    private fun hideSystemUI() {
        // WindowCompat을 사용하여 현재 창의 InsetsController를 가져옵니다.
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // 시스템 바(상단 상태 바, 하단 내비게이션 바)가 나타나는 동작을 설정합니다.
        // BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE: 스와이프 하면 잠깐 나왔다가 다시 사라짐
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // 상태 바와 내비게이션 바를 숨깁니다.
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

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