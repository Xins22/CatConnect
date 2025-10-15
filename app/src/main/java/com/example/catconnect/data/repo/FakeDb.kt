package com.example.catconnect.data.repo

import com.example.catconnect.data.model.*

object FakeDb {
    // === USER AKTIF (sesuaikan dgn modelmu: id, name, photoUrl, bio) ===
    val currentUser = User(
        "u1",
        "Alif",
        "https://picsum.photos/200",
        "Cat lover"
    )

    // === KOLEKSI DUMMY (SAMAKAN DENGAN MODEL SAAT INI) ===
    val users = mutableListOf(
        currentUser,
        User("u2", "Bela", "https://picsum.photos/201", "Meow enthusiast")
    )

    // Post constructor milikmu: (id, userId, title, breed, ageMonth, caption, photoUrl, likes)
    val posts = mutableListOf(
        Post("p1","u1","Snowy","Persian",8,"Snowy suka tidur 😺","https://picsum.photos/seed/c1/800/500",12),
        Post("p2","u2","Milo","Siamese",14,"Main laser tiap sore","https://picsum.photos/seed/c2/800/500",5)
    )

    // Koleksi lain siap dipakai fitur berikutnya (sesuaikan model yg sudah kamu buat)
    val comments = mutableListOf<Comment>()
    val events = mutableListOf<Event>()
    val adoptions = mutableListOf<Adoption>()
    val rooms = mutableListOf<Room>()
    val messages = mutableListOf<Message>()
    val likedByUser = mutableSetOf<String>() // jejak like post oleh user aktif
}
