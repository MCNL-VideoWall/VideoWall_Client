package com.example.video_wall_client.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainBroadcastModel: ViewModel(){
    private val PORT = 65535
    private val TIMEOUT_MS = 3000

    private val _serverIP = MutableLiveData<String>()
    val serverIP: LiveData<String> get() = _serverIP

    fun broadcastMessage(msg: String){
        viewModelScope.launch(Dispatchers.IO){
            var socket: DatagramSocket? = null


            socket = DatagramSocket()
            socket.broadcast = true
            socket.soTimeout = TIMEOUT_MS

            val data = msg.toByteArray(Charsets.UTF_8)
            val address = InetAddress.getByName("255.255.255.255")
            val packet = DatagramPacket(data, data.size, address, PORT)

            Log.d("com.example.video_wall_client.viewmodel.MainBroadcastModel", "sendBroadcastMessage: $msg")


            val buffer = ByteArray(1024)
            val receivedPacket = DatagramPacket(buffer, buffer.size)

            var receivedIP: String? = null
            var message: String? = null

            while(true){
                try {
                    socket.send(packet)
                    socket.receive(receivedPacket)

                    receivedIP= receivedPacket.address.hostAddress
                    message = String(receivedPacket.data, 0, receivedPacket.length)

                    if(!receivedIP.isNullOrEmpty() && message == "VIDEO_WALL_CONNECT_RESPONSE") break

                } catch (e: SocketTimeoutException) {
                    Log.d("Broadcast", "3second. (Timeout)")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Broadcast", "Error: ${e.message}")
                }
            }

            Log.d("Discovery*", "Find ServerAddress! IP: ${receivedIP}, Msg: $message")

            _serverIP.postValue(receivedIP)
            socket?.close()

        }
    }
}