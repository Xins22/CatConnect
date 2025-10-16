package com.example.catconnect.ui.feed
import com.example.catconnect.data.model.Post

fun Post.toUi(): PostUi = PostUi(
    id = this.id,                // penting: pakai id asli
    photoUrl = this.photoUrl,    // tampilkan gambar
    authorName = null,           // isi nama user kalau kamu punya (opsional)
    breed = this.breed,
    ageMonth = this.ageMonth,
    caption = this.caption,
    likes = this.likes ?: 0
)
