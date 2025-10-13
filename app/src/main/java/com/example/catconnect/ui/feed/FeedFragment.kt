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
import android.widget.TextView

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val vm: FeedViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ambil view dari layout
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        // pasang adapter
        adapter = PostAdapter(
            onClick = { post ->
                val b = Bundle().apply { putString("postId", post.id) }
                findNavController().navigate(R.id.postDetailFragment, b)
            },
            onLike = { post ->
                val updated = post.copy(likes = post.likes + 1)
                com.example.catconnect.data.repo.FakeRepository.updatePost(updated)
                Snackbar.make(requireView(), "Liked", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        com.example.catconnect.data.repo.FakeRepository.updatePost(post)
                    }
                    .show()
            }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter


        // observe dummy data
        vm.posts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            view.findViewById<TextView>(R.id.tvEmpty).visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }


        // tombol add -> ke form
        fab.setOnClickListener {
            findNavController().navigate(R.id.addPostFragment)
        }
    }
}
