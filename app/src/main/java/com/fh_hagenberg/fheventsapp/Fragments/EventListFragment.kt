package com.fh_hagenberg.fheventsapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var eventList: List<EventModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventListAdapter

    private var eventType: String? = null

    companion object {
        private const val ARG_EVENT_TYPE = "event_type"

        const val EVENT_TYPE_OFFICIAL = "official"
        const val EVENT_TYPE_PRIVATE = "private"
        const val EVENT_TYPE_OFFICIAL_PAST = "official_past"
        const val EVENT_TYPE_PRIVATE_PAST = "private_past"

        // Funktion zum Erstellen einer Instanz des Fragments mit einem bestimmten Event-Typ
        fun newInstance(eventType: String): EventListFragment {
            val fragment = EventListFragment()
            val args = Bundle()
            args.putString(ARG_EVENT_TYPE, eventType)
            fragment.arguments = args
            return fragment
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

        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Hier wird der Event-Typ aus den Argumenten abgerufen
        eventType = arguments?.getString(ARG_EVENT_TYPE)

        loadEvents()
    }

    private fun loadEvents() {
        GlobalScope.launch(Dispatchers.IO) {
            // Hier wird basierend auf dem Event-Typ die entsprechende Funktion aufgerufen
            eventList = when (eventType) {
                EVENT_TYPE_OFFICIAL -> firebaseRepository.getUpcomingOfficialEvents()
                EVENT_TYPE_PRIVATE -> firebaseRepository.getUpcomingPrivateEvents()
                EVENT_TYPE_OFFICIAL_PAST -> firebaseRepository.getPastOfficialEvents()
                EVENT_TYPE_PRIVATE_PAST -> firebaseRepository.getPastPrivateEvents()
                else -> firebaseRepository.getEvents()
            }

            withContext(Dispatchers.Main) {
                eventAdapter = EventListAdapter(eventList)
                recyclerView.adapter = eventAdapter
            }
        }
    }
}