package com.tip.itso.z.vision

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class VisionPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager)
{
    override fun getItem(position: Int): Fragment {
        return when (position)
        {
            0 -> {
                StatusFragment()
            }
            1 -> FunctionsFragment()
            else -> {
                return MapFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position)
        {
            0 -> "Status"
            1 -> "Functions"
            else -> {
                return "Map"
            }
        }
    }
}