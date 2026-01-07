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
import java.util.UUID
import androidx.recyclerview.widget.LinearLayoutManager


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

        binding.btnSend.setOnClickListener{
            broadcastModel.broadcastMessage(message)
        }

        broadcastModel.serverIP.observe(this){ip ->
            if(!ip.isNullOrEmpty()){
                GlobalState.serverIP = ip

                Log.d("MainActivity*", "serverIP: $ip")

                connectWebSocket()
            }
        }

    }

    fun connectWebSocket(){

        // connet()
        val targetIP = GlobalState.serverIP ?: return

        // connet 직후 uuid랑 HELLO 메시지 보내기
        WebSocketManager.setListener { message -> runOnUiThread{
            Log.d("MainActivity*", "message: $message")
            MessageManager.onMessageReceived(message)

        } }

        WebSocketManager.connect(targetIP){
            Log.d("MainActivity*", "connect success")

        }
        // data cmd 추출
        // when()
    }



}

