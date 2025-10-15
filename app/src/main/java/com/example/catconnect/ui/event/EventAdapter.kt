package com.example.catconnect.ui.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.catconnect.R
import com.example.catconnect.data.model.Event
import java.text.SimpleDateFormat
import java.util.*

class EventAdapter(
    private val onClick: (Event) -> Unit,
    private val onLongPress: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(o: Event, n: Event) = o.id == n.id
        override fun areContentsTheSame(o: Event, n: Event) = o == n
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val iv: ImageView = v.findViewById(R.id.ivBanner)
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvMeta: TextView = v.findViewById(R.id.tvMeta)
        val tvDesc: TextView = v.findViewById(R.id.tvDesc)
    }

    private val df = SimpleDateFormat("EEE, d MMM yyyy • HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_event, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val e = getItem(pos)
        h.iv.load(e.bannerUrl)
        h.tvTitle.text = e.title
        h.tvMeta.text = "${df.format(Date(e.startAt))} • ${e.locationName}"
        h.tvDesc.text = e.desc

        h.itemView.setOnClickListener { onClick(e) }
        h.itemView.setOnLongClickListener { onLongPress(e); true }
    }

}
