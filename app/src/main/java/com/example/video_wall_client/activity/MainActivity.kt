package com.example.video_wall_client.activity

import com.example.video_wall_client.viewmodel.MainBroadcastModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.video_wall_client.databinding.ActivityMainBinding
import androidx.activity.viewModels
import com.example.video_wall_client.data.GlobalState
import com.example.video_wall_client.viewmodel.MessageManager
import com.example.video_wall_client.viewmodel.WebSocketManager
import android.util.Log
import android.view.View
import org.json.JSONObject
import java.util.UUID

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private val message: String = "VIDEO_WALL_CONNECT_REQUEST"

    private val randomUUID: String = UUID.randomUUID().toString()


    private val broadcastModel: MainBroadcastModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalState.clientUuid = randomUUID

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()
        showLoadingState(true)

        broadcastModel.broadcastMessage(message)

        broadcastModel.serverIP.observe(this){ip ->
            if(!ip.isNullOrEmpty()){
                GlobalState.serverIP = ip

                Log.d("MainActivity*", "serverIP: $ip")
                showLoadingState(false)

                connectWebSocket()
            }
        }

    }

    private fun showLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.layoutLoading.visibility = View.VISIBLE
            binding.layoutConnected.visibility = View.GONE
        } else {
            binding.layoutLoading.visibility = View.GONE
            binding.layoutConnected.visibility = View.VISIBLE
        }
    }

    fun connectWebSocket(){

        val targetIP = GlobalState.serverIP ?: return

        WebSocketManager.setListener { message -> runOnUiThread{
            Log.d("MainActivity*", "message: $message")
            MessageManager.onMessageReceived(message)
            updateUI()
        } }

        WebSocketManager.connect(targetIP){
            Log.d("MainActivity*", "connect success")
        }

        // data cmd 추출
        // when()
    }

    fun updateUI() {
        runOnUiThread {
            // GlobalState에 있는 값을 가져와서 XML(binding)에 꽂아줍니다.

            // 1. 마커 ID 표시
            val id = GlobalState.markerId
            binding.tvMarkerId.text = id?.toString() ?: "대기중" // null이면 "대기중" 표시


            // 3. 비트맵 처리 (이미지뷰가 있다면)
            // binding.imageView.setImageBitmap(...)
        }
    }

}

