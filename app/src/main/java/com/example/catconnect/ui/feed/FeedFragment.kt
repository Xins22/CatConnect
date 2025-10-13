package com.example.catconnect.ui.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.catconnect.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val vm: FeedViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ambil view dari layout
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        // pasang adapter
        adapter = PostAdapter { post ->
            val b = Bundle().apply { putString("postId", post.id) }
            findNavController().navigate(R.id.postDetailFragment, b)
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // observe dummy data
        vm.posts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            Snackbar.make(view, "Loaded ${list.size} posts", Snackbar.LENGTH_SHORT).show()
        }

        // tombol add -> ke form
        fab.setOnClickListener {
            findNavController().navigate(R.id.addPostFragment)
        }
    }
}
