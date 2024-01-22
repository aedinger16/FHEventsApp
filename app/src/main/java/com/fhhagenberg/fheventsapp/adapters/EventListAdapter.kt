package com.fhhagenberg.fheventsapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.fhhagenberg.fheventsapp.api.models.EventModel
import com.fhhagenberg.fheventsapp.activities.EventDetailsActivity
import com.fhhagenberg.fheventsapp.R

import java.text.SimpleDateFormat
import java.util.Date

class EventListAdapter(private val eventList: List<EventModel>) :
    RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewEventTitle)
        val dateTextView: TextView = itemView.findViewById(R.id.textViewEventDate)
        val participantsTextView: TextView = itemView.findViewById(R.id.textViewEventParticipants)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)

        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentItem = eventList[position]

        holder.titleTextView.text = currentItem.title

        val date = Date(currentItem.datetime?.seconds?.times(1000) ?: 0)
        holder.dateTextView.text = SimpleDateFormat.getInstance().format(date)

        holder.participantsTextView.text = currentItem.participants?.size.toString().plus(" Teilnehmer")

        holder.itemView.setOnClickListener {
            val context: Context = holder.itemView.context
            val intent = Intent(context, EventDetailsActivity::class.java)
            intent.putExtra("eventId", currentItem.eventId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = eventList.size
}