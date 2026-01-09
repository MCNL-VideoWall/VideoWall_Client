package com.example.video_wall_client.activity

import android.content.Context
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.video_wall_client.data.GlobalState
import com.example.video_wall_client.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding
    private var player: ExoPlayer? = null
    private var multicastLock: WifiManager.MulticastLock? = null

    // 서버에서 보내주는 포트 번호에 맞게 수정 필요 (예: 1234)
    private val UDP_PORT = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        acquireMulticastLock() // 멀티캐스트 패킷 수신을 위한 락 획득
        initializePlayer()
    }

    private fun acquireMulticastLock() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        multicastLock = wifiManager.createMulticastLock("VideoWallMulticastLock")
        multicastLock?.setReferenceCounted(true)
        multicastLock?.acquire()
        Log.d("VideoActivity", "Multicast Lock Acquired")
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        val ip = GlobalState.multicastIP
        if (ip.isNullOrEmpty()) {
            Log.e("VideoActivity", "Multicast IP is null")
            finish()
            return
        }

        // UDP 주소 설정 (Ex: udp://224.0.0.1:1234)
        val udpUri = Uri.parse("udp://${ip}:${UDP_PORT}")
        Log.d("VideoActivity", "Playing from: $udpUri")

        val loadControl = androidx.media3.exoplayer.DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                500,   // Min Buffer (ms)
                2000,  // Max Buffer (ms)
                100,   // Playback Start Buffer (ms) - 시작할 때 0.1초만 있으면 됨
                100    // Re-buffer (ms)
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(this))
            .setLoadControl(loadControl)
            .build()
            .apply {
                binding.playerView.player = this
                val mediaItem = MediaItem.fromUri(udpUri)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true // 자동 재생 시작
            }
    }

    private fun releasePlayer() {
        player?.let {
            it.release()
            player = null
        }
    }

    private fun releaseMulticastLock() {
        if (multicastLock?.isHeld == true) {
            multicastLock?.release()
            Log.d("VideoActivity", "Multicast Lock Released")
        }
    }

    private fun hideSystemUI() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onStop() {
        super.onStop()
        // 앱이 백그라운드로 가면 정지 (필요에 따라 onPause/onDestroy에서 처리)
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        releaseMulticastLock()
    }
}