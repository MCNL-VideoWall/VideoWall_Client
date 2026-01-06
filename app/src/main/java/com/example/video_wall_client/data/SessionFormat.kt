package com.example.video_wall_client.data

data class SessionFormat(
    val sessionId: String, // Int -> String 변경
    val sessionName: String,
    val currentClientCount: Int
)