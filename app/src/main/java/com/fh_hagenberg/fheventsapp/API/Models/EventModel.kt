package com.fh_hagenberg.fheventsapp.API.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class EventModel(
    val eventId: String? = null,
    val datetime: Timestamp? = null,
    val description: String? = null,
    val interestedUsers: List<String>? = null,
    val links: List<String>? = null,
    val location: GeoPoint? = null,
    val locationName: String? = null,
    val organizerId: String? = null,
    val participants: List<String>? = null,
    val title: String? = null,
    val type: String? = null
)