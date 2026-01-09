package com.example.video_wall_client.viewmodel

import VideoWallData
import android.util.Log
import com.example.video_wall_client.data.GlobalState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject

object MessageManager {
    const val TAG: String = "MessageManager*"

    private val _eventFlow = MutableSharedFlow<String>(
        replay = 0, extraBufferCapacity = 1
    )

    val eventFlow = _eventFlow.asSharedFlow()

    fun onMessageReceived(text: String) {
        val jsonObject = JSONObject(text)

        val msgType: String = jsonObject["type"].toString()

        when (msgType) {
            "WELCOME" -> {
                Log.d(TAG, "WELCOME")
                val data = jsonObject.getJSONObject("data").toString()
                parseWelcomeData(data)
            }

            "SHOW_MARKER" -> {
                Log.d(TAG, "SHOW_MARKER")
                showMarker()
            }

            "START_PLAYBACK" -> {
                Log.d(TAG, "START_PLAYBACK")
                startPlayback()
            }

            "ERROR" -> {
                Log.d(TAG, "ERROR")
                error()
            }
        }
    }

    // WELCOME 메시지를 처리하는 함수
    fun parseWelcomeData(data: String) {
        try {
            val gson = Gson()
            val response = gson.fromJson(data, VideoWallData::class.java)

            val id = response.markerId
            val ip = response.multicastIP
            val bitmap = response.arucoBitmap

            GlobalState.markerId = id
            GlobalState.multicastIP = ip
            GlobalState.bitmap = bitmap

            // 전달받은 데이터 로그로 출력
            Log.d(TAG, "ID: $id, IP: $ip")
            var markerText: String = ""
            for (i in 0 until bitmap.size)
                for (j in 0 until bitmap[i].size)
                    markerText += bitmap[i][j].toString()
            Log.d(TAG, "MarkerText: $markerText")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showMarker() {
        _eventFlow.tryEmit("SHOW_MARKER")
    }

    fun startPlayback() {

    }

    fun error() {

    }
}