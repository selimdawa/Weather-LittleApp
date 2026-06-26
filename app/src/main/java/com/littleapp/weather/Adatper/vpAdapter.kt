package com.littleapp.weather.Adatper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class vpAdapter(fa: FragmentActivity, private val list: List<Fragment>) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment = list[position]
}