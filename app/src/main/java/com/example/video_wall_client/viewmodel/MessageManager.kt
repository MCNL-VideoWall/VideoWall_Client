package com.example.video_wall_client.viewmodel

import org.json.JSONObject
import org.json.JSONArray
import android.util.Log

object MessageManager{

    fun onMessageReceived(text: String){
        val jsonObject = JSONObject(text)

        val msgType: String = jsonObject["type"].toString()

        when(msgType){
            "WELCOME" -> {
                val data = jsonObject["data"].toString() // sessionList:Dict[]
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

    }

    fun showMarker(){


    }

    fun startPlayback(){

    }

    fun error(){

    }
}