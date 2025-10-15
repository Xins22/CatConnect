package com.example.catconnect.data.model

enum class ReportType { POST, COMMENT, USER }

data class Report(
    val id: String,
    val type: ReportType,
    val targetId: String,     // id post/comment/user yang dilaporkan
    val reporterId: String,   // id pelapor
    val reason: String,
    val createdAt: Long = System.currentTimeMillis()
)
