package com.fh_hagenberg.fheventsapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.fh_hagenberg.fheventsapp.Adapters.EventsPagerAdapter
import com.fh_hagenberg.fheventsapp.R
import com.google.android.material.tabs.TabLayout

class EventsHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_events_history, container, false)

        // ViewPager und TabLayout einrichten
        setupViewPager(rootView)

        return rootView
    }

    private fun setupViewPager(rootView: View) {
        val viewPager: ViewPager = rootView.findViewById(R.id.viewPager)
        val adapter = EventsPagerAdapter(childFragmentManager)

        adapter.addFragment(EventListFragment.newInstance(EventListFragment.EVENT_TYPE_OFFICIAL_PAST), "Official Events")
        adapter.addFragment(EventListFragment.newInstance(EventListFragment.EVENT_TYPE_PRIVATE_PAST), "Private Events")

        viewPager.adapter = adapter

        val tabLayout: TabLayout = rootView.findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }
}
