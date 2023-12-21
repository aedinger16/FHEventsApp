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

        // UI-Elemente initialisieren
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        editTextFirstName = findViewById(R.id.editTextFirstName)
        editTextLastName = findViewById(R.id.editTextLastName)
        spinnerCourse = findViewById(R.id.spinnerCourse)
        buttonRegister = findViewById(R.id.buttonRegister)

        // Spinner (Dropdown) für den Studiengang (Course) einrichten
        setupCourseSpinner()

        // Register-Button OnClickListener
        buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setupCourseSpinner() {
        val courses = arrayOf("Artificial Intelligence Solutions", "Automotive Computing", "Design of Digital Products", "Digital Arts",
            "Hardware-Software-Design", "Kommunikation, Wissen, Medien", "Medientechnik und -design", "Medizin- und Bioinformatik", "Mobile Computing",
            "Sichere Informationssysteme", "Software Engineering", "Data Science und Engineering", "Embedded Systems Design",
            "Energy Informatics", "Human-Centered Computing", "Information Engineering und -Management", "Information Security Management",
            "Interactive Media")
        courses.sort()

        // ArrayAdapter für Spinner erstellen
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, courses)

        // Adapter dem Spinner zuweisen
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
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password and confirmation do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Benutzer mit FirebaseRepository erstellen
        GlobalScope.launch(Dispatchers.Main) {
            val user = UserModel(
                userId = "", // Das wird von Firebase erstellt
                name = firstName.plus(" ").plus(lastName),
                profileImageUrl = "https://robohash.org/62.240.134.175.png", // TODO Just for testing
                role = "student",
                course = course,
            )

            val result: OperationResult = repository.saveUser(user)

            if (result.success) {
                // Benutzer erfolgreich erstellt
                Toast.makeText(this@RegistrationActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
                finish()
            } else {
                // Fehler bei der Benutzererstellung
                Toast.makeText(
                    this@RegistrationActivity,
                    "Registration failed. ${result.errorMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}