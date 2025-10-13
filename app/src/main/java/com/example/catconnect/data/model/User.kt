package com.example.catconnect.data.model

data class User(
    val id: String,
    val name: String,
    val photoUrl: String? = null,
    val bio: String = ""
)
