package com.example.catconnect.data.model

data class Event(
    val id: String,
    val title: String,
    val desc: String,
    val startAt: Long,
    val locationName: String,
    val bannerUrl: String,
    val organizerId: String,
    val attendees: MutableList<String> = mutableListOf()
)


