package com.example.catconnect.ui.event

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import coil.load
import com.example.catconnect.R
import com.example.catconnect.data.repo.FakeRepository
import java.text.SimpleDateFormat
import java.util.*

class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {

    private val df = SimpleDateFormat("EEE, d MMM yyyy • HH:mm", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = requireArguments().getString("eventId") ?: return
        val e = FakeRepository.events.value?.firstOrNull { it.id == eventId } ?: return

        val iv = view.findViewById<ImageView>(R.id.ivBanner)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMeta = view.findViewById<TextView>(R.id.tvMeta)
        val tvDesc = view.findViewById<TextView>(R.id.tvDesc)

        iv.load(e.bannerUrl)
        tvTitle.text = e.title
        tvMeta.text = "${df.format(Date(e.startAt))} • ${e.locationName}"
        tvDesc.text = e.desc
    }
}
