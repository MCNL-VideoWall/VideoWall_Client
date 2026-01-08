package com.example.video_wall_client.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.video_wall_client.data.GlobalState
import com.example.video_wall_client.databinding.ActivityMainBinding
import com.example.video_wall_client.viewmodel.MainBroadcastModel
import com.example.video_wall_client.viewmodel.MessageManager
import com.example.video_wall_client.viewmodel.WebSocketManager
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val message: String = "VIDEO_WALL_CONNECT_REQUEST"      // 서버 탐색&연결에 사용할 메시지 문자열
    private val randomUUID: String = UUID.randomUUID().toString()   // 앱 시작 시 랜덤 UUID 생성
    private val broadcastModel: MainBroadcastModel by viewModels()
    // broadcastModel 변수의 값을 구하는 로직을 viewModels()에게 위임
    // -> 화면 회전 등으로 인한 onDestroy 이후에도 새 Activity가 자동으로 viewModel()을 호출하면 데이터가 유지됨

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalState.clientUuid = randomUUID     // GlobalState에 UUID 저장

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateUI()
        showLoadingState(true)

        broadcastModel.broadcastMessage(message)
        // 브로드캐스트로 메시지 송신 후 서버측의 메시지 대기
        // 서버 메시지 수신 후 _serverIP(private val)를 update -> serverIP도 update됨

        // broadcastModel의 serverIP가 update되었을 때 이 블록이 실행됨
        broadcastModel.serverIP.observe(this) { ip ->   // observe가 전달해준 값(변경된 serverIP의 값)을 ip라는 이름으로 받음
            if (!ip.isNullOrEmpty()) {      // 만약 ip가 유의미한 값일 경우
                GlobalState.serverIP = ip       // GlobalState의 serverIP를 ip의 값으로 update

                Log.d("MainActivity*", "serverIP: $ip")

                showLoadingState(false)     // UI 전환
                connectWebSocket()          // 서버 IP 확정 이후 WebSocket 연결
            }
        }

        collectNavigationEvents()   // 내비게이션 이벤트를 수집, 발생한 이벤트가 "SHOW_MARKER"일 경우 MarkerActivity로 넘어가기

    }

    // 내비게이션 이벤트 수집하고, 다른 액티비티로 넘어가는 함
    private fun collectNavigationEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                MessageManager.eventFlow.collect { event ->
                    Log.d("MainActivity*", "Event received: $event")
                    when (event) {
                        "SHOW_MARKER" -> {
                            val intent = Intent(this@MainActivity, MarkerActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    // WebSocket 연결하는 함수 (WebSocketManager 사용)
    fun connectWebSocket() {
        val targetIP = GlobalState.serverIP ?: return

        WebSocketManager.setListener { message ->
            runOnUiThread {
                Log.d("MainActivity*", "message: $message")
                MessageManager.onMessageReceived(message)
                updateUI()
            }
        }

        WebSocketManager.connect(targetIP) {
            Log.d("MainActivity*", "connect success")
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

    fun updateUI() {
        runOnUiThread {
            val id = GlobalState.markerId
            binding.tvMarkerId.text = id?.toString() ?: "대기중"

        }
    }
}

