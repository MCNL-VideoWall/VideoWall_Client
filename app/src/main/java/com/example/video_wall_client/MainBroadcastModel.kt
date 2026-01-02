import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.*

class MainBroadcastModel: ViewModel(){
    private val PORT = 65535

    fun sendBroadcastMessage(msg: String){
        viewModelScope.launch(Dispatchers.IO){
            var socket: DatagramSocket? = null

            try{
                socket = DatagramSocket()
                socket.broadcast = true

                val data = msg.toByteArray(Charsets.UTF_8)

                val address = InetAddress.getByName("255.255.255.255")

                val packet = DatagramPacket(data, data.size, address, PORT)
                socket.send(packet)

                Log.d("MainBroadcastModel", "sendBroadcastMessage: $msg")
            }catch (e: Exception){
                e.printStackTrace()
                Log.e("MainBroadcastModel", "sendBroadcastMessage: ${e.message}")
            }finally {
                socket?.close()
            }
        }
    }
}