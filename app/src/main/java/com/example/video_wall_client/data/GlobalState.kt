package com.example.video_wall_client.data

object GlobalState{
    var serverIP: String? = null
    var clientUuid: String? = null

    var markerId: Int? = null
    var multicastIP: String? = null
    var bitmap: List<List<Int>>? = null
}