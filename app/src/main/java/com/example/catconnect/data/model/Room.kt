package com.example.catconnect.data.model

data class Room(
    val id: String,
    val memberIds: List<String>,
    var lastMsg: String,
    var updatedAt: Long
)
