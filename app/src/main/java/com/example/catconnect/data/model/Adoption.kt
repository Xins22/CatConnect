package com.example.catconnect.data.model

data class Adoption(
    val id: String,
    val type: AdoptionType,      // OFFER | REQUEST
    val userId: String,
    var petName: String,
    var breed: String,
    var ageMonth: Int,
    var description: String,
    var contact: String,
    var status: AdoptionStatus = AdoptionStatus.PENDING
)

enum class AdoptionType { OFFER, REQUEST }
enum class AdoptionStatus { PENDING, APPROVED, REJECTED }
