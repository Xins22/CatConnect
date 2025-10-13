package com.example.catconnect.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.catconnect.R
import com.example.catconnect.data.model.Post
import com.example.catconnect.data.repo.FakeRepository
import com.example.catconnect.databinding.FragmentAddPostBinding
import com.google.android.material.snackbar.Snackbar

class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private var editingPostId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // cek apakah ini mode edit
        editingPostId = arguments?.getString("postId")
        editingPostId?.let { id ->
            FakeRepository.getPost(id)?.let { p ->
                binding.etTitle.setText(p.title)
                binding.etBreed.setText(p.breed)
                binding.etAge.setText(p.ageMonth.toString())
                binding.etCaption.setText(p.caption)
                binding.btnSave.text = getString(R.string.update, "Update") // atau langsung "Update"
            }
        }

        binding.btnSave.setOnClickListener {
            if (!validate()) return@setOnClickListener

            val title = binding.etTitle.text.toString().trim()
            val breed = binding.etBreed.text.toString().trim()
            val age = binding.etAge.text.toString().toInt()
            val caption = binding.etCaption.text.toString().trim()

            val editingId = editingPostId
            if (editingId == null) {
                val newPost = Post(
                    id = "p" + System.currentTimeMillis(),
                    userId = FakeRepository.currentUser.id,
                    title = title,
                    breed = breed,
                    ageMonth = age,
                    caption = caption,
                    photoUrl = "https://picsum.photos/seed/newcat${System.currentTimeMillis()}/800/500"
                )
                FakeRepository.addPost(newPost)
                Snackbar.make(view, "Post saved", Snackbar.LENGTH_SHORT).show()
            } else {
                val old = FakeRepository.getPost(editingId)!!
                val updated = old.copy(
                    title = title, breed = breed, ageMonth = age, caption = caption
                )
                FakeRepository.updatePost(updated)
                Snackbar.make(view, "Post updated", Snackbar.LENGTH_SHORT).show()
            }
            findNavController().navigateUp()
        }
    }

    private fun validate(): Boolean {
        var ok = true
        with(binding) {
            tilTitle.error = if (etTitle.text.isNullOrBlank()) { ok = false; "Required" } else null
            tilBreed.error = if (etBreed.text.isNullOrBlank()) { ok = false; "Required" } else null
            val age = etAge.text?.toString()?.toIntOrNull()
            tilAge.error = if (age == null || age < 0) { ok = false; "Age >= 0" } else null
        }
        return ok
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
