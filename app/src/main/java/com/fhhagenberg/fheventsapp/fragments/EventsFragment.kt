package com.fhhagenberg.fheventsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

import com.fhhagenberg.fheventsapp.adapters.EventsPagerAdapter
import com.fhhagenberg.fheventsapp.R
import com.google.android.material.tabs.TabLayout

class EventsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_events, container, false)

        setupViewPager(rootView)

        return rootView
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
            EventListFragment.newInstance(EventListFragment.EVENT_TYPE_OFFICIAL),
            getString(R.string.title_official_events)
        )
        adapter.addFragment(
            EventListFragment.newInstance(EventListFragment.EVENT_TYPE_PRIVATE),
            getString(R.string.title_private_events)
        )

        return adapter
    }
}
