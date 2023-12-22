package com.fh_hagenberg.fheventsapp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.fh_hagenberg.fheventsapp.API.Models.EventModel
import com.fh_hagenberg.fheventsapp.API.Repositories.FirebaseRepository
import com.fh_hagenberg.fheventsapp.API.UserModel
import com.fh_hagenberg.fheventsapp.Adapters.ParticipantsAdapter
import com.fh_hagenberg.fheventsapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventDetailsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var locationTextView: TextView

    private lateinit var participantsRecyclerView: RecyclerView
    private lateinit var interestedRecyclerView: RecyclerView

    private lateinit var buttonJoinEvent: Button
    private lateinit var buttonInterestedInEvent: Button
    private lateinit var buttonLeaveEvent: Button

    private lateinit var buttonEdit: FloatingActionButton

    private lateinit var event: EventModel
    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        initViews()
        setupClickListeners()

        val eventId = intent.getStringExtra("eventId")

        buttonEdit.setOnClickListener {
            val editEventIntent = Intent(this, CreateEventActivity::class.java)
            editEventIntent.putExtra("eventId", eventId)
            startActivity(editEventIntent)
        }

        if (eventId != null) {
            loadEventDetails(eventId)
        }
    }

    private fun initViews() {
        titleTextView = findViewById(R.id.textViewEventTitle)
        dateTextView = findViewById(R.id.textViewEventDate)
        descriptionTextView = findViewById(R.id.textViewDescription)
        locationTextView = findViewById(R.id.textViewLocation)

        participantsRecyclerView = findViewById(R.id.recyclerViewParticipants)
        interestedRecyclerView = findViewById(R.id.recyclerViewInterested)

        buttonJoinEvent = findViewById(R.id.buttonJoinEvent)
        buttonInterestedInEvent = findViewById(R.id.buttonInterestedInEvent)
        buttonLeaveEvent = findViewById(R.id.buttonLeaveEvent)

        buttonEdit = findViewById(R.id.buttonEdit)
    }

    private fun setupClickListeners() {
        buttonJoinEvent.setOnClickListener { handleJoinEvent() }
        buttonInterestedInEvent.setOnClickListener { handleInterestedInEvent() }
        buttonLeaveEvent.setOnClickListener { handleLeaveEvent() }
    }

    private fun isEventInFuture(): Boolean {
        val currentTime = System.currentTimeMillis()
        return (event.datetime?.toDate()?.time ?: 0) > currentTime
    }

    private fun loadEventDetails(eventId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            event = firebaseRepository.getEvent(eventId) ?: return@launch

            withContext(Dispatchers.Main) {
                updateUI()
            }
        }
    }

    private fun updateUI() {
        titleTextView.text = event.title
        dateTextView.text = event.datetime?.toDate().toString()
        locationTextView.text = event.locationName
        descriptionTextView.text = event.description

        val isCurrentUserEventCreator = event.organizerId == firebaseRepository.getCurrentUserId()

        buttonEdit.visibility = if (isCurrentUserEventCreator) View.VISIBLE else View.GONE

        val isEventInFuture = isEventInFuture()

        buttonJoinEvent.visibility = if (isEventInFuture) View.VISIBLE else View.GONE
        buttonInterestedInEvent.visibility = if (isEventInFuture) View.VISIBLE else View.GONE
        buttonLeaveEvent.visibility = if (isEventInFuture) View.VISIBLE else View.GONE

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadAndShowParticipants()
        loadAndShowInterested()
    }

    private fun loadAndShowParticipants() {
        val participants = event.participants ?: emptyList()

        GlobalScope.launch(Dispatchers.IO) {
            val participantsList = firebaseRepository.getUsersByIdList(participants)

            withContext(Dispatchers.Main) {
                setupRecyclerView(participantsRecyclerView, participantsList)
            }
        }
    }

    private fun loadAndShowInterested() {
        val interestedUsers = event.interestedUsers ?: emptyList()

        GlobalScope.launch(Dispatchers.IO) {
            val participantsList = firebaseRepository.getUsersByIdList(interestedUsers)

            withContext(Dispatchers.Main) {
                setupRecyclerView(interestedRecyclerView, participantsList)
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, participantsList: List<UserModel>) {
        recyclerView.layoutManager = LinearLayoutManager(this@EventDetailsActivity)
        recyclerView.adapter = ParticipantsAdapter(participantsList)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        event.location?.let {
            val location = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(MarkerOptions().position(location).title(event.locationName))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun handleJoinEvent() {
        if (event.participants?.contains(firebaseRepository.getCurrentUserId()) == true) {
            showAlert("Du hast bereits am Event teilgenommen.")
            return
        }

        if (event.interestedUsers?.contains(firebaseRepository.getCurrentUserId()) == true) {
            event.interestedUsers?.let {
                val updatedInterestedUsers = it.toMutableList()
                updatedInterestedUsers.remove(firebaseRepository.getCurrentUserId())
                event.interestedUsers = updatedInterestedUsers
                updateInterestedUsersList()
            }
        }

        event.participants?.let {
            val updatedParticipants = it.toMutableList()
            updatedParticipants.add(firebaseRepository.getCurrentUserId().toString())
            event.participants = updatedParticipants
            updateParticipantsList()
        }

        showAlert("Du nimmst erfolgreich am Event teil.")
    }

    private fun handleInterestedInEvent() {
        if (event.participants?.contains(firebaseRepository.getCurrentUserId()) == true) {
            event.participants?.let {
                val updatedParticipants = it.toMutableList()
                updatedParticipants.remove(firebaseRepository.getCurrentUserId())
                event.participants = updatedParticipants
                updateParticipantsList()
            }
        }

        if (event.interestedUsers?.contains(firebaseRepository.getCurrentUserId()) == true) {
            showAlert("Du bist bereits als Interessent registriert.")
            return
        }

        event.interestedUsers?.let {
            val updatedInterestedUsers = it.toMutableList()
            updatedInterestedUsers.add(firebaseRepository.getCurrentUserId().toString())
            event.interestedUsers = updatedInterestedUsers
            updateInterestedUsersList()
        }

        showAlert("Du bist jetzt als Interessent registriert.")
    }

    private fun handleLeaveEvent() {
        if (event.participants?.contains(firebaseRepository.getCurrentUserId()) == true) {
            event.participants?.let {
                val updatedParticipants = it.toMutableList()
                updatedParticipants.remove(firebaseRepository.getCurrentUserId())
                event.participants = updatedParticipants
                updateParticipantsList()
                showAlert("Du hast das Event verlassen.")
            }
        } else if (event.interestedUsers?.contains(firebaseRepository.getCurrentUserId()) == true) {
            event.interestedUsers?.let {
                val updatedInterestedUsers = it.toMutableList()
                updatedInterestedUsers.remove(firebaseRepository.getCurrentUserId())
                event.interestedUsers = updatedInterestedUsers
                updateInterestedUsersList()
                showAlert("Du hast das Interesse am Event zurückgezogen.")
            }
        } else {
            showAlert("Du bist weder Teilnehmer noch Interessent dieses Events.")
        }
    }

    private fun updateParticipantsList() {
        GlobalScope.launch(Dispatchers.IO) {
            firebaseRepository.updateEventParticipants(event.eventId, event.participants)
        }
        loadAndShowParticipants()
    }

    private fun updateInterestedUsersList() {
        GlobalScope.launch(Dispatchers.IO) {
            firebaseRepository.updateEventInterestedUsers(event.eventId, event.interestedUsers)
        }
        loadAndShowInterested()
    }

    private fun showAlert(message: String) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}