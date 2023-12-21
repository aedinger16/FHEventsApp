package com.fh_hagenberg.fheventsapp.API.Interfaces

import com.fh_hagenberg.fheventsapp.API.Helper.OperationResult
import com.fh_hagenberg.fheventsapp.API.Models.EventModel
import com.fh_hagenberg.fheventsapp.API.UserModel

interface Repository {
    suspend fun getEvent(eventId: String): EventModel?
    suspend fun getEvents(): List<EventModel>
    suspend fun getUpcomingOfficialEvents(): List<EventModel>
    suspend fun getUpcomingPrivateEvents(): List<EventModel>
    suspend fun getPastOfficialEvents(): List<EventModel>
    suspend fun getPastPrivateEvents(): List<EventModel>
    suspend fun saveEvent(event: EventModel) : OperationResult
    suspend fun getUser(userId: String): UserModel?
    suspend fun saveUser(user: UserModel) : OperationResult
}