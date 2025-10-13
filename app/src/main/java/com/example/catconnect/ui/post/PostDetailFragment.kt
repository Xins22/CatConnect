package com.example.catconnect.ui.post

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.catconnect.R
import com.example.catconnect.data.repo.FakeRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private var postId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postId = arguments?.getString("postId")

        val img = view.findViewById<ImageView>(R.id.img)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)
        val tvCaption = view.findViewById<TextView>(R.id.tvCaption)
        val btnEdit = view.findViewById<MaterialButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)
        val btnShare = view.findViewById<MaterialButton>(R.id.btnShare)


        val post = postId?.let { FakeRepository.getPost(it) }
        if (post == null) {
            Snackbar.make(view, "Post not found", Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        img.load(post.photoUrl) { crossfade(true) }
        tvTitle.text = post.title
        tvSubtitle.text = "${post.breed} • ${post.ageMonth} mo • ${post.likes} likes"
        tvCaption.text = post.caption

        btnEdit.setOnClickListener {
            val b = Bundle().apply { putString("postId", post.id) }
            findNavController().navigate(R.id.addPostFragment, b) // reuse form utk edit
        }

        btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Post?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Delete") { _, _ ->
                    FakeRepository.deletePost(post.id)
                    Snackbar.make(view, "Post deleted", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        btnShare.setOnClickListener {
            val text = "${post.title} — ${post.breed}, ${post.ageMonth} mo\n${post.caption}"
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, text)
            }
            startActivity(android.content.Intent.createChooser(intent, "Share post via"))
        }
    }
}
