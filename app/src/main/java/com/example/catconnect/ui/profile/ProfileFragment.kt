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
        adapter = PostAdapter { post ->
            val b = Bundle().apply { putString("postId", post.id) }
            findNavController().navigate(R.id.postDetailFragment, b)
        }
        binding.rvMyPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPosts.adapter = adapter

        vm.myPosts.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
