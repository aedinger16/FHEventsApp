package com.fh_hagenberg.fheventsapp.Activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

import com.fh_hagenberg.fheventsapp.API.Models.EventModel
import com.fh_hagenberg.fheventsapp.API.Repositories.FirebaseRepository
import com.fh_hagenberg.fheventsapp.R
import com.google.firebase.Timestamp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var editTextEventTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextLinks: EditText
    private lateinit var editTextLocationName: EditText
    private lateinit var editTextLocationAddress: EditText
    private lateinit var editTextDateTime: EditText

    private lateinit var textViewCreateEventHeader : TextView

    private lateinit var buttonCreateEvent: Button
    private lateinit var buttonDeleteEvent: Button

    private lateinit var calendar: Calendar

    private var eventId: String? = null
    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        eventId = intent.getStringExtra("eventId")
        buttonCreateEvent = findViewById(R.id.buttonCreateEvent)
        buttonDeleteEvent = findViewById(R.id.buttonDeleteEvent)

        editTextEventTitle = findViewById(R.id.editTextEventTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextLinks = findViewById(R.id.editTextLinks)
        editTextLocationName = findViewById(R.id.editTextLocationName)
        editTextLocationAddress = findViewById(R.id.editTextLocationAddress)
        editTextDateTime = findViewById(R.id.editTextDateTime)
        textViewCreateEventHeader = findViewById(R.id.textViewCreateEventHeader)

        if (eventId != null) {
            loadEventData(eventId!!)

            buttonCreateEvent.text = "Aktualisieren"
            textViewCreateEventHeader.text = "Event bearbeiten"
            buttonDeleteEvent.isVisible = true
        }

        calendar = Calendar.getInstance()

        buttonCreateEvent.setOnClickListener { createEvent() }
        buttonDeleteEvent.setOnClickListener { deleteEvent() }
        editTextDateTime.setOnClickListener { showDateTimePickerDialog() }
    }

    private fun showDateTimePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                showTimePickerDialog()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeEditText()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        timePickerDialog.show()
    }

    private fun updateDateTimeEditText() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val formattedDateTime = dateFormat.format(calendar.time)
        editTextDateTime.setText(formattedDateTime)
    }

    private fun createEvent() {
        val title = editTextEventTitle.text.toString()
        val datetime = Timestamp(calendar.time)
        val description = editTextDescription.text.toString()
        val linksText = editTextLinks.text.toString()
        val links = linksText.split("\n").map { it.trim() }
        val locationName = editTextLocationName.text.toString()
        val locationAddress = editTextLocationAddress.text.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val geopoint = convertAddressToGeoPoint(locationAddress)
            if (geopoint != null) {
                val currentUserId = firebaseRepository.getCurrentUserId()
                val currentUser = firebaseRepository.getUser(currentUserId.toString())

                val eventType = if (currentUser?.role == "admin") {
                    "official"
                } else {
                    "private"
                }

                val event = EventModel(
                    title = title,
                    datetime = datetime,
                    description = description,
                    links = links,
                    locationName = locationName,
                    location = geopoint,
                    interestedUsers = emptyList(),
                    organizerId = firebaseRepository.getCurrentUserId(),
                    participants = emptyList(),
                    type = eventType
                )

                if (eventId != null) {
                    event.eventId = eventId
                    updateEvent(event)
                } else {
                    saveEvent(event)
                }
            } else {
                showToast("Adresse konnte nicht gefunden werden")
            }
        }
    }

    private fun deleteEvent() {
        if (eventId != null) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Confirm Deletion")
            alertDialogBuilder.setMessage("Are you sure you want to delete this event?")
            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    val result = firebaseRepository.deleteEvent(eventId!!)
                    if (result.success) {
                        showToast("Event deleted successfully")
                        finish()
                    } else {
                        showToast("Failed to delete event. ${result.errorMessage}")
                    }
                }
            }
            alertDialogBuilder.setNegativeButton("No") { _, _ ->
            }

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun updateEvent(event: EventModel) {
        GlobalScope.launch(Dispatchers.IO) {
            val existingEvent = firebaseRepository.getEventById(event.eventId ?: "")
            if (existingEvent != null) {
                event.participants = existingEvent.participants
                event.interestedUsers = existingEvent.interestedUsers

                firebaseRepository.updateEvent(event)
                finish()
            } else {
                showToast("Failed to update event. Event not found.")
            }
        }
    }

    private fun loadEventData(eventId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val loadedEvent = firebaseRepository.getEventById(eventId)
            if (loadedEvent != null) {
                runOnUiThread {
                    editTextEventTitle.setText(loadedEvent.title)
                    editTextDescription.setText(loadedEvent.description)
                    editTextLinks.setText(loadedEvent.links?.joinToString("\n"))
                    editTextLocationName.setText(loadedEvent.locationName)
                    editTextLocationAddress.setText(getAddressFromGeoPoint(loadedEvent.location))

                    calendar.time = loadedEvent.datetime?.toDate()!!
                    updateDateTimeEditText()
                }
            } else {
                showToast("Event konnte nicht geladen werden")
            }
        }
    }

    private fun getAddressFromGeoPoint(geoPoint: com.google.firebase.firestore.GeoPoint?): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addressList = geocoder.getFromLocation(geoPoint?.latitude ?: 0.0, geoPoint?.longitude ?: 0.0, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                addressList[0].getAddressLine(0) ?: ""
            } else {
                ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    private fun convertAddressToGeoPoint(address: String): com.google.firebase.firestore.GeoPoint? {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocationName(address, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                return com.google.firebase.firestore.GeoPoint(addressList[0].latitude, addressList[0].longitude)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveEvent(event: EventModel) {
        GlobalScope.launch(Dispatchers.IO) {
            firebaseRepository.saveEvent(event)
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}