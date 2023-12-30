package com.fhhagenberg.fheventsapp.api.Models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class EventModel(
    var eventId: String? = null,
    val datetime: Timestamp? = null,
    val description: String? = null,
    var interestedUsers: List<String>? = null,
    val links: List<String>? = null,
    val location: GeoPoint? = null,
    val locationName: String? = null,
    val organizerId: String? = null,
    var participants: List<String>? = null,
    val title: String? = null,
    val type: String? = null
)