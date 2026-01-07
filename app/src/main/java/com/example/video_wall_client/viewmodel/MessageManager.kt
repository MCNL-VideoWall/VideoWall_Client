package com.example.video_wall_client.viewmodel
import ServerResponse
import com.google.gson.Gson
import org.json.JSONObject
import org.json.JSONArray
import android.util.Log
import com.example.video_wall_client.data.GlobalState

object MessageManager{

    fun onMessageReceived(text: String){
        val jsonObject = JSONObject(text)

        val msgType: String = jsonObject["type"].toString()

        when(msgType){
            "WELCOME" -> {
                val data = jsonObject["data"].toString()
                parseWelcomeData(data)
            }
             "SHOW_MARKER" -> {
                 showMarker()
             }
             "START_PLAYBACK" -> {
                 startPlayback()
             }
             "ERROR" -> {
                 error()
             }
        }
    }

    fun parseWelcomeData(data: String){
        try {
            val gson = Gson()
            val response = gson.fromJson(data, ServerResponse::class.java)

            val id = response.data.markerId
            val ip = response.data.multicastIp
            val bitmap = response.data.arucoBitmap

            GlobalState.markerId = id
            GlobalState.multicastIP = ip
            GlobalState.bitmap = bitmap

            Log.d("Parsing", "ID: $id, IP: $ip")

            var markerText: String =""
            for(i in 0 until bitmap.size) {
                for (j in 0 until bitmap[i].size) {
                    markerText += bitmap[i][j].toString()
                }
            }

            Log.d("Parsing", "MarkerText: $markerText")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showMarker(){


    }

    fun startPlayback(){

    }

    fun error(){

    }
}