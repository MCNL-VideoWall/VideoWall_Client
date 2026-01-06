package com.example.video_wall_client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.video_wall_client.databinding.ItemSessionBinding
import com.example.video_wall_client.data.SessionFormat

// 매개변수: 방을 클릭했을 때 실행할 함수(onClick)를 받아옴
class SessionAdapter(private val onClick: (SessionFormat) -> Unit) :
    RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    private var sessionList: List<SessionFormat> = listOf()

    // 데이터를 갱신하는 함수
    fun submitList(newList: List<SessionFormat>) {
        sessionList = newList
        notifyDataSetChanged() // 리스트 갱신 알림
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        // item_session.xml을 가져와서 뷰로 만듦
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(sessionList[position])
    }

    override fun getItemCount(): Int = sessionList.size

    inner class SessionViewHolder(private val binding: ItemSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(session: SessionFormat) {
            // 데이터 연결
            binding.tvSessionName.text = session.sessionName
            binding.tvSessionId.text = "ID: ${session.sessionId}"
            binding.tvClientCount.text = "${session.currentClientCount}명 참여 중"

            // 클릭 이벤트 처리 (방을 누르면 onClick 실행)
            binding.root.setOnClickListener {
                onClick(session)
            }
        }
    }
}