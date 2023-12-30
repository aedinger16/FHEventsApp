package com.fhhagenberg.fheventsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fhhagenberg.fheventsapp.api.UserModel
import com.fhhagenberg.fheventsapp.R
import com.google.firebase.storage.FirebaseStorage

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

        private val profilePictureImageView: ImageView = itemView.findViewById(R.id.imageViewProfilePicture)

        fun bind(participant: UserModel) {
            nameTextView.text = participant.name
            courseTextView.text = participant.course

            val storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(participant.userId + ".jpg")
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    Glide.with(itemView.context)
                        .load(downloadUrl)
                        .circleCrop()
                        .into(profilePictureImageView)
                }
                .addOnFailureListener {
                }
        }
    }
}