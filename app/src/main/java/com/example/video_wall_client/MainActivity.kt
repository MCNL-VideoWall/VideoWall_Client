package com.example.video_wall_client

import MainBroadcastModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.video_wall_client.databinding.ActivityMainBinding
import androidx.activity.viewModels
import GlobalState
import MessageManager
import SessionData
import WebSocketManager
import android.util.Log
import java.util.UUID
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity(), MessageManager.MessageListener {
    private lateinit var binding: ActivityMainBinding
    private val message: String = "VIDEO_WALL_CONNECT_REQUEST"
    private lateinit var sessionAdapter: SessionAdapter

    private val randomUUID: String = UUID.randomUUID().toString()


    private val broadcastModel: MainBroadcastModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        GlobalState.clientUuid = randomUUID

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        MessageManager.listener = this

        sessionAdapter = SessionAdapter { session ->
            // 방을 클릭했을 때 실행될 동작
            Log.d("MainActivity", "방 클릭됨: ${session.sessionId}")
            MessageManager.sendSessionJoin(session.sessionId) // 참가 요청 전송
        }

        // 2. 리사이클러뷰에 어댑터와 레이아웃 매니저 연결
        binding.rvSessionList.apply {
            adapter = sessionAdapter
            layoutManager = LinearLayoutManager(this@MainActivity) // 세로 스크롤
        }

        binding.btnSend.setOnClickListener{
            broadcastModel.broadcastMessage(message)
        }

        broadcastModel.serverIP.observe(this){ip ->
            if(!ip.isNullOrEmpty()){
                GlobalState.serverIP = ip

                Log.d("MainActivity*", "serverIP: $ip")

                connectWebSocket()
                sessionManager()
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


            MessageManager.sendHello() // 서버: WELCOME
        }
        // data cmd 추출
        // when()
    }

    fun sessionManager(){

        // create 버튼 누를시 밑에 함수 실행
        binding.btnCreateSession.setOnClickListener {
            val name: String = "Test"
            MessageManager.sendSessionCreate(name) // 서버: SESSION_CREATED
        }

        // if(id) 방장 참가자 구분

        // join 버튼
        binding.btnJoinSession.setOnClickListener {
            val id: String = "0"
            MessageManager.sendSessionJoin(id) // 서버: SESSION_JOINED
        }

    }

    override fun onSessionListReceived(sessionList: List<SessionData>) {
        Log.d("MainActivity", "Session list arrive: ${sessionList.size}개")

        sessionList.forEach {
            Log.d("Session", "ID: ${it.sessionId}, Name: ${it.sessionName}")
        }

        sessionAdapter.submitList(sessionList)
        // RecyclerView Adapter에 sessionList 넣고 갱신하기
    }

    override fun onSessionCreated(sessionID: String) {
        Log.d("MainActivity", "Room create success! ID: $sessionID")
        // 방장이 되었으므로 대기 화면으로 이동하거나 UI 변경
        MessageManager.sendSessionListRequest()
    }

    override fun onSessionJoined(data: String) {
        Log.d("MainActivity", "Room join success! Data: $data")
        // 참여자가 되었으므로 대기 화면으로 이동
    }

}

