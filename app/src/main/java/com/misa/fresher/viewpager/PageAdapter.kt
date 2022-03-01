package com.misa.fresher.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter(fm:FragmentManager):FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        when(position)
        {
            0->{
                return FirstFragment()
            }
            1->{
                return SecondFragment()
            }
            2->{
                return ThirdFragment()
            }
            else -> {
                return FirstFragment()
            }
        }
    }
    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Người nhận"
            }
            1 -> {
                return "Giao hàng"
            }
            2 -> {
                return "Gói hàng"
            }
        }
        return super.getPageTitle(position)
    }
}