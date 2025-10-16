package com.example.catconnect.ui.post

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.catconnect.R
import com.example.catconnect.data.model.ReportType
import com.example.catconnect.data.repo.FakeRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private lateinit var postId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Ambil argumen aman ---
        postId = requireArguments().getString("postId") ?: run {
            Snackbar.make(view, "Post not found", Snackbar.LENGTH_SHORT).show()
            // TUNDA popBack supaya tidak bentrok dengan transaksi push
            view.post { if (isAdded) findNavController().popBackStack() }
            return
        }

        val post = FakeRepository.getPost(postId) ?: run {
            Snackbar.make(view, "Post not found", Snackbar.LENGTH_SHORT).show()
            view.post { if (isAdded) findNavController().popBackStack() }
            return
        }

        // ====== Bind UI dasar ======
        val img = view.findViewById<ImageView>(R.id.img)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSubtitle = view.findViewById<TextView>(R.id.tvSubtitle)
        val tvCaption = view.findViewById<TextView>(R.id.tvCaption)

        img.load(post.photoUrl) { crossfade(true) }
        tvTitle.text = post.title
        tvSubtitle.text = "${post.breed} • ${post.ageMonth} mo • ${post.likes} likes"
        tvCaption.text = post.caption

        // ====== Overflow menu (⋮) ======
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_post_detail, menu)

                val isOwner = FakeRepository.currentUser.id == post.userId
                val alreadyReported = FakeRepository.hasReported(post.id, FakeRepository.currentUser.id)

                menu.findItem(R.id.action_edit).isVisible = isOwner
                menu.findItem(R.id.action_delete).isVisible = isOwner
                menu.findItem(R.id.action_report).isVisible = !isOwner && !alreadyReported
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_edit -> {
                        val b = Bundle().apply { putString("postId", post.id) }
                        findNavController().navigate(R.id.addPostFragment, b)
                        true
                    }
                    R.id.action_delete -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Delete Post?")
                            .setMessage("This action cannot be undone.")
                            .setPositiveButton("Delete") { _, _ ->
                                FakeRepository.deletePost(post.id)
                                Snackbar.make(requireView(), "Post deleted", Snackbar.LENGTH_SHORT).show()
                                // TUNDA popBack setelah dialog tertutup & transaksi aman
                                view?.post { if (isAdded) findNavController().popBackStack() }
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                        true
                    }
                    R.id.action_share -> {
                        val text = "${post.title} — ${post.breed}, ${post.ageMonth} mo\n${post.caption}"
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, text)
                        }
                        startActivity(android.content.Intent.createChooser(intent, "Share post via"))
                        true
                    }
                    R.id.action_report -> {
                        val reasons = arrayOf(
                            "Spam",
                            "Konten tidak pantas",
                            "Penipuan",
                            "Kekerasan pada hewan",
                            "Lainnya…"
                        )
                        var chosen = reasons[0]
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Laporkan Post")
                            .setSingleChoiceItems(reasons, 0) { _, which -> chosen = reasons[which] }
                            .setPositiveButton("Kirim") { _, _ ->
                                FakeRepository.addReport(
                                    targetId = post.id,
                                    reporterId = FakeRepository.currentUser.id,
                                    reason = chosen,
                                    type = ReportType.POST
                                )
                                Snackbar.make(requireView(), "Laporan terkirim. Terima kasih 🙏", Snackbar.LENGTH_LONG).show()
                            }
                            .setNegativeButton("Batal", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // ====== Komentar ======
        val rv = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvComments)
        val et = view.findViewById<EditText>(R.id.etComment)
        val btnSend = view.findViewById<View>(R.id.btnSend)

        val commentAdapter = CommentAdapter { comment ->
            val mine = comment.userId == FakeRepository.currentUser.id
            if (mine) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Hapus komentar ini?")
                    .setPositiveButton("Hapus") { _, _ ->
                        FakeRepository.deleteComment(comment.id)
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = commentAdapter

        FakeRepository.commentsFor(postId).observe(viewLifecycleOwner) { list ->
            commentAdapter.submitList(list)
            if (list.isNotEmpty()) rv.scrollToPosition(list.lastIndex)
        }

        btnSend.setOnClickListener {
            val text = et.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            FakeRepository.addComment(postId, text)
            et.text?.clear()
        }
    }
}
