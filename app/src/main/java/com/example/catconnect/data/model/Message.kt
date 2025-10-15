package com.example.catconnect.data.model

data class Message(
    val id: String,
    val roomId: String,
    val fromUserId: String,
    val text: String,
    val createdAt: Long
)