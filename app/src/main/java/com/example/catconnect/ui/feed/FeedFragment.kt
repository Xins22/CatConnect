package com.example.catconnect.ui.feed

import android.os.Bundle
import android.view.*
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
import com.example.catconnect.data.model.Post
import com.example.catconnect.ui.feed.toUi   // mapper Post -> PostUi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val vm: FeedViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    // simpan list Post asli untuk keperluan toggleLike (repo masih menerima Post)
    private var lastPosts: List<Post> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab = view.findViewById<FloatingActionButton>(R.id.fabAdd)

        // --- Inisialisasi adapter ---
        adapter = PostAdapter(
            onClick = { postUi ->
                val b = Bundle().apply { putString("postId", postUi.id) }
                findNavController().navigate(R.id.postDetailFragment, b)
            },
            onLike = { postUi ->
                val original = lastPosts.firstOrNull { it.id == postUi.id }
                if (original != null) {
                    val after = com.example.catconnect.data.repo.FakeRepository.toggleLike(original)
                    val newLikes = (after.likes ?: 0)

                    // update UI list locally
                    val idx = adapter.currentList.indexOfFirst { it.id == postUi.id }
                    if (idx != -1) {
                        val newList = adapter.currentList.toMutableList()
                        newList[idx] = newList[idx].copy(likes = newLikes)
                        adapter.submitList(newList)
                    }

                    val msg = if (newLikes > postUi.likes) "Liked" else "Unliked"
                    com.google.android.material.snackbar.Snackbar
                        .make(requireView(), msg, com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    com.google.android.material.snackbar.Snackbar
                        .make(requireView(), "Item tidak ditemukan", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                        .show()
                }
            }

        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        // --- Observasi data postingan ---
        vm.filteredPosts.observe(viewLifecycleOwner) { list: List<Post> ->
            lastPosts = list
            adapter.submitList(list.map { it.toUi() })
            view.findViewById<View>(R.id.emptyState).visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        // --- FAB tambah posting ---
        fab.setOnClickListener {
            findNavController().navigate(R.id.addPostFragment)
        }

        // --- Menu (Search dan Event) ---
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
                        vm.setQuery(q.orEmpty())
                        sv.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        vm.setQuery(newText.orEmpty())
                        return true
                    }
                })

                sv.setOnCloseListener {
                    vm.setQuery("")
                    false
                }
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean =
                when (item.itemId) {
                    R.id.action_events -> {
                        findNavController().navigate(R.id.action_feed_to_eventList)
                        true
                    }
                    else -> false
                }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}
