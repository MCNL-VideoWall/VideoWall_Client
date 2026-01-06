import org.json.JSONObject
import org.json.JSONArray
import android.util.Log

object MessageManager{

    interface MessageListener {
        fun onSessionListReceived(sessionList: List<SessionData>)
        fun onSessionCreated(sessionID: String)
        fun onSessionJoined(sessionID: String)
        // fun onSessionJoined(clientID: Int, sessionList: List<SessionData>)

    }

    var listener: MessageListener? = null

    fun sendHello() {
        val json = JSONObject().apply {
            put("type", "HELLO")
        }
        WebSocketManager.sendMessage(json.toString())
    }

    // sesssion request 필요 없음. 서버에서 갱신이 필요한 때에 보내주면 됨.
    fun sendSessionListRequest(){
        val json = JSONObject().apply {
            put("type", "SESSION_LIST_REQ")
        }
        WebSocketManager.sendMessage(json.toString())
    }

    fun sendSessionCreate(name: String?){
        val json = JSONObject().apply {
            put("type", "SESSION_CREATE")
            put("data", "$name") // 나중에 사용자에게 입력받게 할 예정
        }
        WebSocketManager.sendMessage(json.toString())
    }

    fun sendSessionJoin(sessionId: String?){
        val json = JSONObject().apply {
            put("type", "SESSION_JOIN")
            put("data", "$sessionId") //
        }
        WebSocketManager.sendMessage(json.toString())
    }

    fun sendSessionLeave(){
        val json = JSONObject().apply {
            put("type", "SESSION_LIST_REQ")
        }
        WebSocketManager.sendMessage(json.toString())
    }

    fun sendStart(){
        val json = JSONObject().apply {
            put("type", "SESSION_LIST_REQ")
        }
        WebSocketManager.sendMessage(json.toString())
    }


    fun onMessageReceived(text: String){
        val jsonObject = JSONObject(text)

        val msgType: String = jsonObject["type"].toString()

        when(msgType){
            "WELCOME" -> {
                val data = jsonObject["data"].toString() // sessionList:Dict[]
                parseSessionList(data)
            }
             "SESSION_CREATED" -> {
                 val data = jsonObject["data"].toString() // SessionID:String (ClientUUID)
                 listener?.onSessionCreated(data)
             }
            "SESSION_LIST_RES" -> {
                val data = jsonObject["data"].toString() // sessionList:Dict[]
                parseSessionList(data)
            }

             "SESSION_JOINED" -> {
                val data = jsonObject["data"].toString() // ClientID: Int, Session_List
                listener?.onSessionJoined(data)
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

    fun parseSessionList(data: String){
        // sessionList: Dict 파싱 작업
        val parsedList = ArrayList<SessionData>()
        try {
            val jsonArray = JSONArray(data)

            for(i in 0 until jsonArray.length()){
                val obj = jsonArray.getJSONObject(i)

                val id = obj.optString("sessionId")
                val name = obj.optString("sessionName", "Unknown Room")
                val count = obj.optInt("currentClientCount", 0)

                parsedList.add(SessionData(id, name, count))
            }

            listener?.onSessionListReceived(parsedList)
        } catch (e: Exception){
            Log.e("MessageManager", "Error parsing session list: ${e.message}")
        }
    }

    fun showMarker(){


    }

    fun startPlayback(){

    }

    fun error(){

    }
}