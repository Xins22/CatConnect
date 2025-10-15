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

class EventListFragment : Fragment(R.layout.fragment_event_list) {

    private lateinit var adapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rv)

        adapter = EventAdapter(
            onClick = { event ->
                val bundle = bundleOf("eventId" to event.id)
                findNavController().navigate(R.id.action_eventList_to_eventDetail, bundle)
            },
            onLongPress = { event ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hapus Event")
                    .setMessage("Apakah kamu yakin ingin menghapus event '${event.title}'?")
                    .setPositiveButton("Hapus") { _, _ ->
                        FakeRepository.deleteEvent(event.id)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        FakeRepository.events.observe(viewLifecycleOwner) { eventList ->
            adapter.submitList(eventList)
        }
    }
}
