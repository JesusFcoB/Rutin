package com.example.rutin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DayPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = WorkoutData.days.size
    override fun createFragment(position: Int): Fragment = DayFragment.newInstance(position)
}