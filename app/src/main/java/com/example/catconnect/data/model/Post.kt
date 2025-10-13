package com.example.catconnect.data.model

data class Post(
    val id: String,
    val userId: String,
    val title: String,
    val breed: String,
    val ageMonth: Int,
    val caption: String,
    val photoUrl: String? = null,
    val likes: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
