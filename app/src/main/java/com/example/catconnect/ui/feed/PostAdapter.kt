package com.example.catconnect.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.catconnect.R
import com.example.catconnect.data.model.Post
import com.google.android.material.button.MaterialButton

class PostAdapter(
    private val onClick: (Post) -> Unit,
    private val onLike: ((Post) -> Unit)? = null
) : ListAdapter<Post, PostAdapter.VH>(DIFF) {

    object DIFF : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(o: Post, n: Post) = o.id == n.id
        override fun areContentsTheSame(o: Post, n: Post) = o == n
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.img)
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvSubtitle: TextView = v.findViewById(R.id.tvSubtitle)
        val btnLike: MaterialButton? = v.findViewById(R.id.btnLike)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH {
        val v = LayoutInflater.from(p.context).inflate(R.layout.item_post, p, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)

        h.img.load(item.photoUrl) { crossfade(true) }
        h.tvTitle.text = item.title
        h.tvSubtitle.text = "${item.breed} • ${item.ageMonth} mo • ${item.likes} likes"

        // tampilkan Like / Unlike
        val liked = com.example.catconnect.data.repo.FakeRepository.isLiked(item.id)
        h.btnLike?.text = if (liked) "Unlike" else "Like"

        h.itemView.setOnClickListener { onClick(item) }
        h.btnLike?.setOnClickListener { onLike?.invoke(item) }
    }

}
