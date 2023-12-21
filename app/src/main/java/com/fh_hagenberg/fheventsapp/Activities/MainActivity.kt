package com.fh_hagenberg.fheventsapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.fh_hagenberg.fheventsapp.Fragments.EventsFragment
import com.fh_hagenberg.fheventsapp.Fragments.EventsHistoryFragment
import com.fh_hagenberg.fheventsapp.Fragments.ProfileFragment
import com.fh_hagenberg.fheventsapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showFragment(EventsFragment())

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_history -> {
                    showFragment(EventsHistoryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.action_events -> {
                    showFragment(EventsFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.action_profile -> {
                    showFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }

        bottomNavigationView.selectedItemId = R.id.action_events
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}