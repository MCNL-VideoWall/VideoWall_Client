import android.util.Log
import okhttp3.*

object WebSocketManager{
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    private var onMessageListener: ((String) -> Unit)? = null

    fun setListener(listener: (String) -> Unit){
        onMessageListener = listener
    }

    fun connect(url: String, onConnect: () -> Unit){
        val request = Request.Builder().url("ws://${url}:8000/ws").build()
        val listener = object : WebSocketListener(){
            override fun onOpen(ws: WebSocket, response: Response){
                webSocket = ws
                Log.d("SocketManager", "connect success")
                onConnect()
            }

            override fun onMessage(ws: WebSocket, text: String){
                Log.d("SocketManager", "message: $text")

                onMessageListener?.invoke(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?){
                Log.d("SocketManager", "failure: ${t.message}")
                webSocket = null
            }
        }
        client.newWebSocket(request, listener)
    }

    fun sendMessage(json: String){
        webSocket?.send(json) ?: Log.e("SocketManager", "WebSocket is not connected")
    }

    fun disconnet(){
        webSocket?.close(1000, "disconnect")
        webSocket = null
    }
}