package com.example.myapplication.view.activity

import Tab2Fragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.view.fragment.Tab1Fragment
import com.example.myapplication.view.fragment.Tab3Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    // Binding class로 view access
    private lateinit var binding: ActivityMainBinding

    // 고정된 앞부분과 뒷부분 문구
    private val commonPrefix = "내가 좋아하는"
    private val commonSuffix = "만 따로 모아봐요."

    // 중간에 들어갈 변화 부분
    private val tabMiddleDescriptions = arrayOf(
        "/자주 연락하는 사람들의 전화번호",
        " 사진들",
        " 장소들"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // ViewPager2 초기화 및 설정
        binding.viewPager.adapter = TabPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false

        // TabLayout과 ViewPager2 연결
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "전화번호부"
                1 -> tab.text = "갤러리"
                2 -> tab.text = "장소"
            }
        }.attach()

        // 초기 설명 설정
        updateDescription(0)

        // 탭 선택 리스너 추가
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateDescription(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Do nothing
            }
        })
    }

    // 선택된 탭에 따라 설명 텍스트를 업데이트하는 함수
    private fun updateDescription(position: Int) {
        if (position in tabMiddleDescriptions.indices) {
            val description = "$commonPrefix${tabMiddleDescriptions[position]}$commonSuffix"
            binding.textDescription.text = description
        } else {
            binding.textDescription.text = " 것들"
        }
    }

    private inner class TabPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return tabMiddleDescriptions.size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Tab1Fragment()
                1 -> Tab2Fragment()
                2 -> Tab3Fragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}