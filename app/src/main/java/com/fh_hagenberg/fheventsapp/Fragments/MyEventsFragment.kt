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

class MyEventsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_my_events, container, false)

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
            EventListFragment.newInstance(EventListFragment.EVENT_TYPE_JOINED_EVENTS),
            getString(R.string.title_joined)
        )
        adapter.addFragment(
            EventListFragment.newInstance(EventListFragment.EVENT_TYPE_MY_EVENTS),
            getString(R.string.title_my_events)
        )

        return adapter
    }
}