import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.*

class MainBroadcastModel: ViewModel(){
    private val PORT = 65535
    private val TIMEOUT_MS = 3000

    fun broadcastMessage(msg: String){
        viewModelScope.launch(Dispatchers.IO){
            var socket: DatagramSocket? = null

            try{
                socket = DatagramSocket()
                socket.broadcast = true
                socket.soTimeout = TIMEOUT_MS

                val data = msg.toByteArray(Charsets.UTF_8)
                val address = InetAddress.getByName("255.255.255.255")
                val packet = DatagramPacket(data, data.size, address, PORT)
                socket.send(packet)

                Log.d("MainBroadcastModel", "sendBroadcastMessage: $msg")


                val buffer = ByteArray(1024)
                val receivedPacket = DatagramPacket(buffer, buffer.size)

                socket.receive(receivedPacket)

                val serverIpAddress = receivedPacket.address.hostAddress
                val message = String(receivedPacket.data, 0, receivedPacket.length)

                Log.d("Discovery", "Find ServerAddress! IP: $serverIpAddress, Msg: $message")

            } catch (e: SocketTimeoutException) {
                Log.e("Broadcast", "3second. (Timeout)")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Broadcast", "Error: ${e.message}")
            } finally {
                // 다 끝나면 소켓 닫기
                socket?.close()
            }
        }
    }
}