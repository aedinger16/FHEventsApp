package com.fhhagenberg.fheventsapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.fhhagenberg.fheventsapp.activities.CreateEventActivity
import com.fhhagenberg.fheventsapp.adapters.EventsPagerAdapter
import com.fhhagenberg.fheventsapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class MyEventsFragment : Fragment() {

    private lateinit var addButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_events, container, false)

        setupViewPager(rootView)

        addButton = rootView.findViewById(R.id.buttonAdd)
        addButton.setOnClickListener {
            launchCreateEventActivity()
        }

        return rootView
    }

    private fun launchCreateEventActivity() {
        val intent = Intent(activity, CreateEventActivity::class.java)
        startActivity(intent)
    }

    private fun setupViewPager(rootView: View) {
        val viewPager: ViewPager = rootView.findViewById(R.id.viewPager)
        viewPager.adapter = createPagerAdapter()

        val tabLayout: TabLayout = rootView.findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun createPagerAdapter(): EventsPagerAdapter {
        val adapter = EventsPagerAdapter(childFragmentManager)

        adapter.addFragment(
            EventListFragment.newInstance(EventListFragment.EVENT_TYPE_MY_EVENTS),
            getString(R.string.title_my_events)
        )
        adapter.addFragment(
            EventListFragment.newInstance(EventListFragment.EVENT_TYPE_JOINED_EVENTS),
            getString(R.string.title_joined)
        )

        return adapter
    }
}