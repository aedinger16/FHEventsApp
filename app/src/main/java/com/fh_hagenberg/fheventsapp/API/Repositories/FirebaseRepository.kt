package com.fh_hagenberg.fheventsapp.API.Repositories

import com.fh_hagenberg.fheventsapp.API.Helper.OperationResult
import com.fh_hagenberg.fheventsapp.API.Interfaces.Repository
import com.fh_hagenberg.fheventsapp.API.Models.EventModel
import com.fh_hagenberg.fheventsapp.API.UserModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

import kotlinx.coroutines.tasks.await
import java.util.Date


class FirebaseRepository : Repository
{
    private val firestore: FirebaseFirestore = Firebase.firestore

    override suspend fun getEvent(eventId: String): EventModel? {
        return try {
            val document = firestore.collection("events").document(eventId).get().await()
            document.toObject(EventModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getEvents(): List<EventModel> {
        return try {
            val querySnapshot = firestore.collection("events").get().await()
            querySnapshot.toObjects(EventModel::class.java)
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

            val events = querySnapshot.toObjects(EventModel::class.java)
            events.filter { event -> event.type.equals("official")}
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getUpcomingPrivateEvents(): List<EventModel> {
        return try {
            val currentTimestamp = Timestamp(Date())

            val querySnapshot = firestore.collection("events")
                .whereGreaterThan("datetime", currentTimestamp)
                .get()
                .await()

            val events = querySnapshot.toObjects(EventModel::class.java)
            events.filter { event -> event.type.equals("private")}
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

            val events = querySnapshot.toObjects(EventModel::class.java)
            events.filter { event -> event.type.equals("official")}
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

    override suspend fun saveUser(user: UserModel): OperationResult {
        return try {
            firestore.collection("users").document(user.userId ?: "").set(user).await()
            OperationResult(success = true)
        } catch (e: Exception) {
            OperationResult(success = false, errorMessage = e.message)
        }
    }
}