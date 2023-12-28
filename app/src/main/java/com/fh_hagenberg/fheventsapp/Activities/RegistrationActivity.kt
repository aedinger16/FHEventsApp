package com.fh_hagenberg.fheventsapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.fh_hagenberg.fheventsapp.API.Helper.OperationResult
import com.fh_hagenberg.fheventsapp.API.Repositories.FirebaseRepository
import com.fh_hagenberg.fheventsapp.API.UserModel
import com.fh_hagenberg.fheventsapp.R
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var spinnerCourse: Spinner
    private lateinit var buttonRegister: Button

    private val repository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        initializeViews()
        setupCourseSpinner()

        buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun initializeViews() {
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        buttonRegister = findViewById(R.id.buttonRegister)
    }

    private fun setupCourseSpinner() {
        val courses = resources.getStringArray(R.array.course_array)
        courses.sort()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, courses)
        spinnerCourse.adapter = adapter
    }

    private fun registerUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val course = spinnerCourse.selectedItem.toString()

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            showToast("Please fill in all fields")
            return
        }

        if (password != confirmPassword) {
            showToast("Password and confirmation do not match")
            return
        }

        if (!isValidEmail(email)) {
            showToast("Invalid FH email format")
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    val userId = firebaseUser?.uid ?: ""

                    GlobalScope.launch(Dispatchers.Main) {
                        val user = UserModel(
                            userId = userId,
                            name = "$firstName $lastName",
                            role = "student",
                            course = course,
                        )

                        val result: OperationResult = repository.saveUser(user)

                        if (result.success) {
                            showToast("Registration successful")
                            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            showToast("Registration failed. ${result.errorMessage}")
                        }
                    }
                } else {
                    showToast("Registration failed. ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[Ss]\\d{10}@(fhooe\\.at|students\\.fh-hagenberg\\.at)\$")
        return regex.matches(email)
    }
}