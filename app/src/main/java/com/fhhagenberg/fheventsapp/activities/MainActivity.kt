package com.fhhagenberg.fheventsapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fhhagenberg.fheventsapp.api.repositories.FirebaseRepository

import com.fhhagenberg.fheventsapp.fragments.EventsFragment
import com.fhhagenberg.fheventsapp.fragments.EventsHistoryFragment
import com.fhhagenberg.fheventsapp.fragments.MyEventsFragment
import com.fhhagenberg.fheventsapp.fragments.ProfileFragment
import com.fhhagenberg.fheventsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var isAdmin: Boolean = false
    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigationView()

        if (savedInstanceState == null) {
            showFragment(EventsFragment())
        }
    }

    override fun onResume() {
        super.onResume()

        val userId = firebaseRepository.getCurrentUserId()
        if (userId != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val userRole = withContext(Dispatchers.IO) {
                    firebaseRepository.getUserRole(userId)
                }
                isAdmin = userRole == "admin"

                updateMenuVisibility()
            }
        }
    }

    private fun updateMenuVisibility() {
        val menu = findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu
        val profileMenuItem = menu.findItem(R.id.action_profile)
        profileMenuItem.isVisible = !isAdmin
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_history -> showFragment(EventsHistoryFragment())
                R.id.action_events -> showFragment(EventsFragment())
                R.id.action_my_events -> showFragment(MyEventsFragment())
                R.id.action_profile -> showFragment(ProfileFragment())
                else -> return@setOnItemSelectedListener false
            }
            true
        }

        bottomNavigationView.selectedItemId = R.id.action_events
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}