package com.example.catconnect.ui.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.catconnect.R
import com.example.catconnect.data.model.Comment
import com.example.catconnect.data.repo.FakeRepository

class CommentAdapter(
    private val onLongPress: (Comment) -> Unit
) : ListAdapter<Comment, CommentAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(o: Comment, n: Comment) = o.id == n.id
        override fun areContentsTheSame(o: Comment, n: Comment) = o == n
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvText: TextView = v.findViewById(R.id.tvText)
        val tvTime: TextView = v.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val c = getItem(pos)
        val u = FakeRepository.getUser(c.userId)
        h.tvName.text = u?.name ?: "User"
        h.tvText.text = c.text
        h.tvTime.text = android.text.format.DateUtils.getRelativeTimeSpanString(c.createdAt)

        h.itemView.setOnLongClickListener {
            onLongPress(c)
            true
        }
    }
}
