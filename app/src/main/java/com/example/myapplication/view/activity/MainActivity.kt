package com.example.myapplication

import Tab2Fragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // ViewPager2 초기화 및 설정
        binding.viewPager.adapter = TabPagerAdapter(this)

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "전화번호부"
                1 -> tab.text = "갤러리"
                2 -> tab.text = "?"
            }
        }.attach()
    }

    private inner class TabPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int {
            return 3 // 탭의 수, 예시로 3개로 설정
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Tab2Fragment()
                1 -> Tab2Fragment()
                2 -> Tab2Fragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
