package com.fh_hagenberg.fheventsapp.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fh_hagenberg.fheventsapp.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonRegister: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Firebase Authentication initialisieren
        auth = FirebaseAuth.getInstance()

        // UI-Elemente initialisieren
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonRegister = findViewById(R.id.buttonRegister)

        // Login-Button OnClickListener
        buttonLogin.setOnClickListener {
            loginUser()
        }

        // Register-Button OnClickListener
        buttonRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Authentication: Email und Passwort einloggen
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login erfolgreich
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Login fehlgeschlagen
                    Toast.makeText(this, "Login failed. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}