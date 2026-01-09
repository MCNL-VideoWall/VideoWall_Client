package com.example.video_wall_client.viewmodel

import android.util.Log
import com.example.video_wall_client.data.GlobalState
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object WebSocketManager {
    const val TAG = "WebSocketManager*"
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()
    private val PORT = 8000

    private var onMessageListener: ((String) -> Unit)? = null

    fun setListener(listener: (String) -> Unit) {
        onMessageListener = listener
    }

    // WebSocket 연결을 시작하는 함수
    fun connect(url: String, onConnect: () -> Unit) {
        Log.d(TAG, "url: $url, uuid: ${GlobalState.clientUuid}")
        val request = Request.Builder().url("ws://$url:$PORT/ws/${GlobalState.clientUuid}").build()
        val listener = object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                webSocket = ws
                Log.d(TAG, "connect success")
                onConnect()
            }

            // 메시지가 왔을 때 동작하는 콜백 함수
            override fun onMessage(ws: WebSocket, text: String) {
                Log.d(TAG, "message: $text")
                MessageManager.onMessageReceived(text)
                onMessageListener?.invoke(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.d(TAG, "failure: ${t.message}")
                webSocket = null
            }
        }
        client.newWebSocket(request, listener)
    }

    fun sendMessage(json: String) {
        webSocket?.send(json) ?: Log.e(TAG, "WebSocket is not connected")
    }

    fun disconnet() {
        webSocket?.close(1000, "disconnect")
        webSocket = null
    }
}