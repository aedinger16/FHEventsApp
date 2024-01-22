package com.fhhagenberg.fheventsapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fhhagenberg.fheventsapp.api.helper.OperationResult
import com.fhhagenberg.fheventsapp.api.repositories.FirebaseRepository
import com.fhhagenberg.fheventsapp.api.UserModel
import com.fhhagenberg.fheventsapp.activities.LoginActivity
import com.fhhagenberg.fheventsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var spinnerCourse: Spinner
    private lateinit var buttonUpdateProfile: Button
    private lateinit var buttonLogout: Button
    private lateinit var imageViewProfile: ImageView

    private val PICK_IMAGE_REQUEST = 1
    private val repository = FirebaseRepository()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initializeViews(view)
        setupCourseSpinner()
        loadUserProfile()
        loadProfileImage()
        return view
    }

    private fun initializeViews(view: View) {
        editTextFirstName = view.findViewById(R.id.editTextFirstName)
        editTextLastName = view.findViewById(R.id.editTextLastName)
        spinnerCourse = view.findViewById(R.id.spinnerCourse)
        buttonUpdateProfile = view.findViewById(R.id.buttonUpdateProfile)
        buttonLogout = view.findViewById(R.id.buttonLogout)
        imageViewProfile = view.findViewById(R.id.imageViewProfile)

        buttonUpdateProfile.setOnClickListener {
            updateUserProfile()
        }

        imageViewProfile.setOnClickListener {
            openImagePicker()
        }

        buttonLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupCourseSpinner() {
        val courses = resources.getStringArray(R.array.course_array)
        courses.sort()

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, courses)
        spinnerCourse.adapter = adapter
    }

    private fun loadUserProfile() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentUser = repository.getUser(repository.getCurrentUserId().toString())

            launch(Dispatchers.Main) {
                currentUser?.let {
                    editTextFirstName.setText(it.name?.split(" ")?.get(0))
                    editTextLastName.setText(it.name?.split(" ")?.get(1))

                    val courseIndex = resources.getStringArray(R.array.course_array).indexOf(it.course)
                    spinnerCourse.setSelection(if (courseIndex != -1) courseIndex else 0)
                }
            }
        }
    }

    private fun loadProfileImage() {
        val storageRef = storage.getReference().child("profile_images").child(repository.getCurrentUserId().toString() + ".jpg")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val downloadUrl = uri.toString()
            Glide.with(requireContext())
                .load(downloadUrl)
                .circleCrop()
                .into(imageViewProfile)
        }.addOnFailureListener {
        }
    }

    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri: Uri = data.data!!
            uploadProfileImage(selectedImageUri)
        }
    }

    private fun uploadProfileImage(selectedImageUri: Uri) {
        val storageRef = storage.getReference().child("profile_images").child(repository.getCurrentUserId().toString() + ".jpg")
        storageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    imageViewProfile.setImageURI(selectedImageUri)
                    loadProfileImage()
                    showToast(getString(R.string.toast_update_picture))
                }
            }
            .addOnFailureListener { exception ->
                showToast(getString(R.string.toast_update_picture_err))
            }
    }

    private fun updateUserProfile() {
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val course = spinnerCourse.selectedItem.toString()

        if (firstName.isEmpty() || lastName.isEmpty()) {
            showToast("Please fill in all fields")
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val user = UserModel(
                userId = repository.getCurrentUserId(),
                name = "$firstName $lastName",
                course = course,
                role = "private",
            )

            val result: OperationResult = repository.saveUser(user)

            if (result.success) {
                showToast("Profile updated successfully")
            } else {
                showToast("Profile update failed. ${result.errorMessage}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}