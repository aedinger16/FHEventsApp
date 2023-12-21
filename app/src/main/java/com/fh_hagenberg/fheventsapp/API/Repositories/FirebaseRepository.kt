package com.fh_hagenberg.fheventsapp.API.Repositories

import com.fh_hagenberg.fheventsapp.API.Helper.OperationResult
import com.fh_hagenberg.fheventsapp.API.Interfaces.Repository
import com.fh_hagenberg.fheventsapp.API.Models.EventModel
import com.fh_hagenberg.fheventsapp.API.UserModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date


class FirebaseRepository : Repository
{
    private val firestore: FirebaseFirestore = Firebase.firestore

    override suspend fun getEvent(eventId: String): EventModel? {
        return try {
            val document = firestore.collection("events").document(eventId).get().await()
            val event = document.toObject(EventModel::class.java)
            return event?.apply {
                this.eventId = document.id
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getEvents(): List<EventModel> {
        return try {
            val querySnapshot = firestore.collection("events").get().await()
            val events = querySnapshot.toObjects(EventModel::class.java)
            for (event in events) {
                event.eventId = querySnapshot.documents.firstOrNull { it.id == event.eventId }?.id
            }
            events
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUpcomingOfficialEvents(): List<EventModel> {
        return try {
            val currentTimestamp = Timestamp.now()

            val querySnapshot = firestore.collection("events")
                .whereGreaterThan("datetime", currentTimestamp)
                .get()
                .await()

            return querySnapshot.documents.mapNotNull { document ->
                val event = document.toObject(EventModel::class.java)
                event?.eventId = document.id
                event.takeIf { it?.type == "official" }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUpcomingPrivateEvents(): List<EventModel> {
        return try {
            val currentTimestamp = Timestamp.now()

            val querySnapshot = firestore.collection("events")
                .whereGreaterThan("datetime", currentTimestamp)
                .get()
                .await()

            return querySnapshot.documents.mapNotNull { document ->
                val event = document.toObject(EventModel::class.java)
                event?.eventId = document.id
                event.takeIf { it?.type == "private" }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPastOfficialEvents(): List<EventModel> {
        return try {
            val currentTimestamp = Timestamp(Date())

            val querySnapshot = firestore.collection("events")
                .whereLessThan("datetime", currentTimestamp)
                .get()
                .await()

            return querySnapshot.documents.mapNotNull { document ->
                val event = document.toObject(EventModel::class.java)
                event?.eventId = document.id
                event.takeIf { it?.type == "private" }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPastPrivateEvents(): List<EventModel> {
        return try {
            val currentTimestamp = Timestamp(Date())

            val querySnapshot = firestore.collection("events")
                .whereLessThan("datetime", currentTimestamp)
                .get()
                .await()

            val events = querySnapshot.toObjects(EventModel::class.java)

            for (event in events) {
                event.eventId = querySnapshot.documents.firstOrNull { it.id == event.eventId }?.id
            }

            events.filter { event -> event.type.equals("private")}
        } catch (e: Exception) {
            emptyList()
        }
    }

    /* USAGE VON SAVE-METHODEN
    val result = repository.saveEvent(event)
    if (result.success) { } else { }*/
    override suspend fun saveEvent(event: EventModel): OperationResult {
        return try {
            firestore.collection("events").document(event.eventId ?: "").set(event).await()
            OperationResult(success = true)
        } catch (e: Exception) {
            OperationResult(success = false, errorMessage = e.message)
        }
    }

    override suspend fun getUser(userId: String): UserModel? {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(UserModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getUsersByIdList(userIds: List<String>): List<UserModel> = withContext(Dispatchers.IO) {
        try {
            val users = mutableListOf<UserModel>()

            for (userId in userIds) {
                val document = firestore.collection("users").document(userId).get().await()
                val user = document.toObject(UserModel::class.java)
                if (user != null) {
                    users.add(user)
                }
            }

            return@withContext users
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    override suspend fun saveUser(user: UserModel): OperationResult {
        return try {
            firestore.collection("users").document(user.userId ?: "").set(user).await()
            OperationResult(success = true)
        } catch (e: Exception) {
            OperationResult(success = false, errorMessage = e.message)
        }
    }

    suspend fun updateEventInterestedUsers(eventId: String?, interestedUsers: List<String>?): OperationResult {
        return try {
            if (eventId != null) {
                firestore.collection("events")
                    .document(eventId)
                    .update("interestedUsers", interestedUsers)
                    .await()
            }
            OperationResult(success = true)
        } catch (e: Exception) {
            OperationResult(success = false, errorMessage = e.message)
        }
    }

    suspend fun updateEventParticipants(eventId: String?, participants: List<String>?): OperationResult {
        return try {
            if (eventId != null) {
                firestore.collection("events")
                    .document(eventId)
                    .update("participants", participants)
                    .await()
            }
            OperationResult(success = true)
        } catch (e: Exception) {
            OperationResult(success = false, errorMessage = e.message)
        }
    }

    fun getCurrentUserId(): String? {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.uid
        } catch (e: Exception) {
            null
        }
    }
}