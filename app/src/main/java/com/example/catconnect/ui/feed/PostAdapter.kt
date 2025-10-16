package com.example.catconnect.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.catconnect.databinding.ItemPostBinding
import com.google.android.material.button.MaterialButton

data class PostUi(
    val id: String,
    val photoUrl: String?,
    val authorName: String?,
    val breed: String?,
    val ageMonth: Int?,
    val caption: String?,
    val likes: Int
)

class PostAdapter(
    private val onClick: (PostUi) -> Unit,
    private val onLike: ((PostUi) -> Unit)? = null,
    private val onMore: ((PostUi) -> Unit)? = null
) : ListAdapter<PostUi, PostAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<PostUi>() {
        override fun areItemsTheSame(oldItem: PostUi, newItem: PostUi) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PostUi, newItem: PostUi) = oldItem == newItem
    }

    inner class VH(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, /* attachToParent = */ false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val b = holder.binding

        // Header
        b.tvAuthor.text = item.authorName.orEmpty().ifBlank { "Anon" }
        val breed = item.breed ?: "-"
        val age = item.ageMonth?.let { "$it mo" } ?: "-"
        b.tvTime.text = "$breed • $age"

        // Avatar (opsional, kalau punya url avatar sendiri — untuk sementara placeholder)
        b.ivAvatar.load(null as String?) {
            crossfade(true)
            placeholder(android.R.color.darker_gray)
        }

        // Foto utama
        b.img.load(item.photoUrl) {
            crossfade(true)
            placeholder(android.R.color.darker_gray)
        }

        // Caption
        b.tvCaption.text = item.caption.orEmpty()

        // Like & Comment
        (b.btnLike as MaterialButton).text = item.likes.toString()
        b.btnLike.setOnClickListener { onLike?.invoke(item) }
        b.btnComment.setOnClickListener { onClick(item) }

        // More
        b.btnMore.setOnClickListener { onMore?.invoke(item) }

        // Klik keseluruhan kartu → detail
        b.root.setOnClickListener { onClick(item) }
    }
}
