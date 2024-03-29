package com.fhhagenberg.fheventsapp.api.interfaces

import com.fhhagenberg.fheventsapp.api.helper.OperationResult
import com.fhhagenberg.fheventsapp.api.models.EventModel
import com.fhhagenberg.fheventsapp.api.UserModel

interface Repository {
    suspend fun getEvent(eventId: String): EventModel?
    suspend fun getEvents(): List<EventModel>
    suspend fun getEventById(eventId: String): EventModel?
    suspend fun getUpcomingOfficialEvents(): List<EventModel>
    suspend fun getUpcomingPrivateEvents(): List<EventModel>
    suspend fun getPastOfficialEvents(): List<EventModel>
    suspend fun getPastPrivateEvents(): List<EventModel>
    suspend fun saveEvent(event: EventModel): OperationResult
    suspend fun deleteEvent(eventId: String): OperationResult
    suspend fun updateEvent(event: EventModel): OperationResult
    suspend fun updateEventInterestedUsers(eventId: String?, interestedUsers: List<String>?): OperationResult
    suspend fun updateEventParticipants(eventId: String?, participants: List<String>?): OperationResult

    suspend fun getUser(userId: String): UserModel?
    suspend fun saveUser(user: UserModel): OperationResult
    suspend fun getUsersByIdList(userIds: List<String>): List<UserModel>
    suspend fun getJoinedEventsFromUser(): List<EventModel>
    suspend fun getEventsFromUser(): List<EventModel>

    fun getCurrentUserId(): String?
}