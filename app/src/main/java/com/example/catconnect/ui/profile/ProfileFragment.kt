package com.example.catconnect.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.catconnect.R
import com.example.catconnect.databinding.FragmentProfileBinding
import com.example.catconnect.ui.feed.PostAdapter
import coil.transform.CircleCropTransformation
import com.example.catconnect.data.session.SessionManager
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.catconnect.data.repo.FakeRepository


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val vm: ProfileViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // header user
        binding.imgAvatar.load(vm.user.photoUrl) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }

        binding.tvName.text = vm.user.name
        binding.tvBio.text  = vm.user.bio

        // list post milik user
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
        binding.rvMyPosts.adapter = adapter

        // 1) layout manager
        binding.rvMyPosts.layoutManager = LinearLayoutManager(requireContext())

// 2) observe data milik user
        vm.myPosts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

// 3) SWIPE-TO-DELETE + UNDO
        val swipe = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return
                val item = adapter.currentList[pos]   // simpan sebelum list berubah

                FakeRepository.deletePost(item.id)

                Snackbar.make(requireView(), "Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { FakeRepository.addPost(item) }
                    .show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvMyPosts)

// 4) biar item terakhir gak ketutup BottomNav
        binding.rvMyPosts.clipToPadding = false
        binding.rvMyPosts.setPadding(
            binding.rvMyPosts.paddingLeft,
            binding.rvMyPosts.paddingTop,
            binding.rvMyPosts.paddingRight,
            binding.rvMyPosts.paddingBottom + resources.getDimensionPixelSize(R.dimen.bottom_nav_space)
        )



        binding.btnLogout.setOnClickListener {
            SessionManager(requireContext()).logout()
            Snackbar.make(view, "Logged out", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
