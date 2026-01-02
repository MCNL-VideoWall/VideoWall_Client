package com.example.video_wall_client

import MainBroadcastModel
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import com.example.video_wall_client.databinding.ActivityMainBinding
import androidx.activity.viewModels
import GlobalState

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val message: String = "VIDEO_WALL_CONNECT_REQUEST"

    private val broadcastModel: MainBroadcastModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener{
            broadcastModel.broadcastMessage(message)
        }
    }
}