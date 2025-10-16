package com.example.catconnect.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.catconnect.R
import com.example.catconnect.data.model.Post
import com.example.catconnect.data.repo.FakeRepository
import com.example.catconnect.data.session.SessionManager
import com.example.catconnect.databinding.FragmentProfileBinding
import com.example.catconnect.ui.feed.PostAdapter
import com.example.catconnect.ui.feed.toUi
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val vm: ProfileViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    // simpan Post asli untuk bridge aksi ke Repository
    private var myLastPosts: List<Post> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ----- Header user -----
        binding.imgAvatar.load(vm.user.photoUrl) {
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        binding.tvName.text = vm.user.name
        binding.tvBio.text  = vm.user.bio

        // ----- Adapter (pakai PostUi) -----
        adapter = PostAdapter(
            onClick = { postUi ->
                val b = Bundle().apply { putString("postId", postUi.id) }
                findNavController().navigate(R.id.postDetailFragment, b)
            },
            onLike = { postUi ->
                val original = myLastPosts.firstOrNull { it.hashCode().toString() == postUi.id }
                if (original != null) {
                    val after = FakeRepository.toggleLike(original)
                    val msg = if ((after.likes ?: 0) > postUi.likes) "Liked" else "Unliked"
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(requireView(), "Item tidak ditemukan", Snackbar.LENGTH_LONG).show()
                }
            }
        )

        binding.rvMyPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPosts.adapter = adapter

        // ----- Observe data milik user (List<Post> -> map ke List<PostUi>) -----
        vm.myPosts.observe(viewLifecycleOwner) { posts: List<Post> ->
            myLastPosts = posts
            adapter.submitList(posts.map { it.toUi() })
            binding.tvEmpty.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
        }

        // ----- Swipe to delete + Undo (bridge ke Post asli) -----
        val swipe = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return

                val itemUi = adapter.currentList[pos] // PostUi
                val original = myLastPosts.firstOrNull { it.hashCode().toString() == itemUi.id }

                if (original == null) {
                    Snackbar.make(requireView(), "Item tidak ditemukan", Snackbar.LENGTH_LONG).show()
                    adapter.notifyItemChanged(pos) // rollback swipe
                    return
                }

                // Delete by original id / object
                FakeRepository.deletePost(original.id)

                Snackbar.make(requireView(), "Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { FakeRepository.addPost(original) }
                    .show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(binding.rvMyPosts)

        // ----- Jaga padding supaya tidak ketutup BottomNav -----
        binding.rvMyPosts.clipToPadding = false
        binding.rvMyPosts.setPadding(
            binding.rvMyPosts.paddingLeft,
            binding.rvMyPosts.paddingTop,
            binding.rvMyPosts.paddingRight,
            binding.rvMyPosts.paddingBottom + resources.getDimensionPixelSize(R.dimen.bottom_nav_space)
        )

        // ----- Logout -----
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
