package com.example.catconnect.ui.event

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.catconnect.R
import com.example.catconnect.data.repo.FakeRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EventListFragment : Fragment(R.layout.fragment_event_list) {
    private lateinit var adapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        adapter = EventAdapter(
            onClick = { e ->
                val b = bundleOf("eventId" to e.id)
                findNavController().navigate(R.id.eventDetailFragment, b)
            },
            onLongPress = { e ->
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Hapus event?")
                    .setPositiveButton("Hapus") { _, _ -> FakeRepository.deleteEvent(e.id) }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        FakeRepository.events.observe(viewLifecycleOwner) { adapter.submitList(it) }

        fab.setOnClickListener { findNavController().navigate(R.id.addEventFragment) }
    }
}
