import org.json.JSONObject

object MessageManager{

    fun sendHello() {
        val json = JSONObject().apply {
            put("type", "HELLO")
        }
        WebSocketManager.sendMessage(json.toString())
    }

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

    fun sendSessionJoin(sessionId: Int?){
        val json = JSONObject().apply {
            put("type", "SESSION_CREATE")
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
             }
            "SESSION_LIST_RES" -> {
                val data = jsonObject["data"].toString() // sessionList:Dict[]
                parseSessionList(data)
            }

             "SESSION_JOINED" -> {
                val data = jsonObject["data"].toString() // ClientID: Int, Session_List
                parseSessionList(data)
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
    }

    fun showMarker(){


    }

    fun startPlayback(){

    }

    fun error(){

    }
}