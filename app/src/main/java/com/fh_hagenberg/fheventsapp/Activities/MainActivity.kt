package com.fh_hagenberg.fheventsapp.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.fh_hagenberg.fheventsapp.Fragments.EventsFragment
import com.fh_hagenberg.fheventsapp.Fragments.EventsHistoryFragment
import com.fh_hagenberg.fheventsapp.Fragments.MyEventsFragment
import com.fh_hagenberg.fheventsapp.Fragments.ProfileFragment
import com.fh_hagenberg.fheventsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigationView()

        if (savedInstanceState == null) {
            showFragment(EventsFragment())
        }
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_history -> showFragment(EventsHistoryFragment())
                R.id.action_events -> showFragment(EventsFragment())
                R.id.action_add_event -> startActivity(Intent(this, CreateEventActivity::class.java))
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