package com.fh_hagenberg.fheventsapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.fh_hagenberg.fheventsapp.API.Repositories.FirebaseRepository
import com.fh_hagenberg.fheventsapp.API.Models.EventModel
import com.fh_hagenberg.fheventsapp.Adapters.EventListAdapter
import com.fh_hagenberg.fheventsapp.R

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventListFragment : Fragment() {

    private val firebaseRepository = FirebaseRepository()
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventListAdapter

    private lateinit var loadingSpinner: ProgressBar

    private var eventType: String? = null

    companion object {
        private const val ARG_EVENT_TYPE = "event_type"

        const val EVENT_TYPE_OFFICIAL = "official"
        const val EVENT_TYPE_PRIVATE = "private"
        const val EVENT_TYPE_OFFICIAL_PAST = "official_past"
        const val EVENT_TYPE_PRIVATE_PAST = "private_past"
        const val EVENT_TYPE_JOINED_EVENTS = "joined_events"
        const val EVENT_TYPE_MY_EVENTS = "my_events"

        fun newInstance(eventType: String): EventListFragment {
            return EventListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EVENT_TYPE, eventType)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        loadingSpinner.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(context)

        eventType = arguments?.getString(ARG_EVENT_TYPE)

        loadEvents()
    }

    override fun onResume() {
        super.onResume()
        loadEvents()
    }

    private fun loadEvents() {
        GlobalScope.launch(Dispatchers.IO) {
            val eventList = when (eventType) {
                EVENT_TYPE_OFFICIAL -> firebaseRepository.getUpcomingOfficialEvents()
                EVENT_TYPE_PRIVATE -> firebaseRepository.getUpcomingPrivateEvents()
                EVENT_TYPE_OFFICIAL_PAST -> firebaseRepository.getPastOfficialEvents()
                EVENT_TYPE_PRIVATE_PAST -> firebaseRepository.getPastPrivateEvents()
                EVENT_TYPE_JOINED_EVENTS -> firebaseRepository.getJoinedEventsFromUser()
                EVENT_TYPE_MY_EVENTS -> firebaseRepository.getEventsFromUser()
                else -> firebaseRepository.getEvents()
            }

            val sortedEventList = eventList.sortedByDescending { it.datetime?.toDate()?.time }

            withContext(Dispatchers.Main) {
                loadingSpinner.visibility = View.GONE
                eventAdapter = EventListAdapter(sortedEventList)
                recyclerView.adapter = eventAdapter
            }
        }
    }
}