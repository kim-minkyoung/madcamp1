package com.example.myapplication.view.activity

import Tab2Fragment
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.model.interfaces.SavePlaceListener
import com.example.myapplication.view.fragment.Tab1Fragment
import com.example.myapplication.view.fragment.Tab3Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), SavePlaceListener {
    private lateinit var binding: ActivityMainBinding

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
                // 아무 작업도 하지 않음
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // 아무 작업도 하지 않음
            }
        })
    }

    private fun updateDescription(position: Int) {
        val descriptions = arrayOf(
            "/자주 연락하는 사람들의 전화번호",
            " 사진들",
            " 장소들"
        )
        if (position in descriptions.indices) {
            val description = "내가 좋아하는${descriptions[position]}만 따로 모아봐요."
            binding.textDescription.text = description
        } else {
            binding.textDescription.text = " 것들"
        }
    }

    private inner class TabPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Tab1Fragment()
                1 -> Tab2Fragment()
                2 -> Tab3Fragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }

    override fun onSavePlaceClicked(address: String?) {
        address?.let {
            val tab3Fragment = supportFragmentManager.fragments.find { it is Tab3Fragment } as? Tab3Fragment
            tab3Fragment?.updateBottomSheet(it, tab3Fragment.marker.position.latitude, tab3Fragment.marker.position.longitude)
            Toast.makeText(this, "장소가 저장 됐어요: $address", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCancelClicked() {
        Toast.makeText(this, "장소 저장이 취소 됐어요", Toast.LENGTH_SHORT).show()
    }
}



