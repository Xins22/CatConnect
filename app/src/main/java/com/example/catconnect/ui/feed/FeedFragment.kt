package com.example.catconnect.ui.feed

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
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

        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)

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
                    }.show()
            }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // ⬅️ GUNAKAN filteredPosts (bukan posts langsung)
        vm.filteredPosts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            view.findViewById<TextView>(R.id.tvEmpty).visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        fab.setOnClickListener { findNavController().navigate(R.id.addPostFragment) }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_feed, menu)
                val item = menu.findItem(R.id.action_search)
                val sv = item.actionView as SearchView
                sv.queryHint = "Cari judul/ras/caption…"

                // restore query
                sv.setQuery(vm.query.value.orEmpty(), false)
                if (!vm.query.value.isNullOrBlank()) {
                    item.expandActionView()
                    sv.clearFocus()
                }

                sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(q: String?): Boolean {
                        vm.setQuery(q.orEmpty()); sv.clearFocus(); return true
                    }
                    override fun onQueryTextChange(newText: String?): Boolean {
                        vm.setQuery(newText.orEmpty()); return true
                    }
                })

                // Saat tombol X ditekan (close), kosongkan query
                sv.setOnCloseListener {
                    vm.setQuery("")
                    false
                }
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = false
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }
}
