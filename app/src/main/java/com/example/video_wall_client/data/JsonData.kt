import com.google.gson.annotations.SerializedName

data class VideoWallData(
    @SerializedName("marker_id") val markerId: Int,
    @SerializedName("aruco_bitmap") val arucoBitmap: List<List<Int>>,
    @SerializedName("multicast_ip") val multicastIP: String
)