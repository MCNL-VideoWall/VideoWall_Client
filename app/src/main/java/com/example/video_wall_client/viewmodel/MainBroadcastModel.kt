package com.example.video_wall_client.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class MainBroadcastModel : ViewModel() {
    private val PORT = 65535
    private val TIMEOUT_MS = 3000
    private val _serverIP = MutableLiveData<String>()   // 내부에서만 수정 가능한 서버 IP
    val serverIP: LiveData<String> get() = _serverIP    // 외부 Activity에서 접근 가능한 읽기용 변수

    val TAG: String = "MainBroadcastModel*"

    // Broadcast로 같은 네트워크상의 서버를 탐색하는 함수
    fun broadcastMessage(msg: String) {
        viewModelScope.launch(Dispatchers.IO) {     // ViewModel 생명주기에 묶인 코루틴 시작, 네트워크 I/O 전용 백그라운드 스레드로 실행

            // UDP 소켓 초기화
            val socket = DatagramSocket()
            socket.broadcast = true
            socket.soTimeout = TIMEOUT_MS

            // 브로드캐스트 패킷 생성
            val data = msg.toByteArray(Charsets.UTF_8)
            val address = InetAddress.getByName("255.255.255.255")
            val packet = DatagramPacket(data, data.size, address, PORT)

            // 서버 응답 수신 준비
            val buffer = ByteArray(1024)
            val receivedPacket = DatagramPacket(buffer, buffer.size)

            // 서버를 찾을 때 까지 반복
            while (true) {
                try {
                    // 메시지 전송
                    socket.send(packet)
                    Log.d(TAG, "sendBroadcastMessage: $msg")

                    // 메시지 수신 (타임아웃 3초)
                    socket.receive(receivedPacket)
                    val receivedIP = receivedPacket.address.hostAddress
                    val message = String(receivedPacket.data, 0, receivedPacket.length)

                    if (!receivedIP.isNullOrEmpty() && message == "VIDEO_WALL_CONNECT_RESPONSE") {  // 수신한 데이터가 기다리던 데이터였을 경우
                        Log.d(TAG, "Find ServerAddress! IP: ${receivedIP}, Msg: $message")

                        _serverIP.postValue(receivedIP)     // 백그라운드 스레드에서 수신한 서버 IP를 LiveData에 전달

                        break
                    }

                } catch (e: SocketTimeoutException) {
                    Log.d(TAG, "Timeout (3 sec)")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, "Error: ${e.message}")
                }
            }
            socket.close()
        }
    }
}