import com.google.gson.annotations.SerializedName

// 내부 'data' 객체
data class VideoWallData(
    @SerializedName("marker_id") val markerId: Int,
    @SerializedName("aruco_bitmap") val arucoBitmap: List<List<Int>>, // 2차원 배열
    @SerializedName("multicast_ip") val multicastIP: String
)