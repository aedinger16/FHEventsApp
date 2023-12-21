package com.fh_hagenberg.fheventsapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fh_hagenberg.fheventsapp.API.UserModel
import com.fh_hagenberg.fheventsapp.R

class ParticipantsAdapter(private val participantsList: List<UserModel>) :
    RecyclerView.Adapter<ParticipantsAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(participantsList[position])
    }

    override fun getItemCount(): Int {
        return participantsList.size
    }

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewParticipantName)
        private val courseTextView: TextView = itemView.findViewById(R.id.textViewParticipantCourse)

        fun bind(participant: UserModel) {
            nameTextView.text = participant.name
            courseTextView.text = participant.course
        }
    }
}