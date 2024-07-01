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
    // 바인딩 클래스를 이용해 뷰에 접근
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
                // 아무 작업도 하지 않음
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // 아무 작업도 하지 않음
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

    // SavePlaceListener 인터페이스 구현
    override fun onSavePlaceClicked(address: String?) {
        // 장소 저장 로직을 처리
        address?.let {
            // BottomSheet에 주소를 업데이트
            val tab3Fragment = supportFragmentManager.findFragmentByTag("f2") as? Tab3Fragment
            tab3Fragment?.updateBottomSheet(it, tab3Fragment.marker.position.latitude, tab3Fragment.marker.position.longitude)
            // 토스트 메시지를 보여줌
            Toast.makeText(this, "Place saved: $address", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCancelClicked() {
        // 취소 로직을 처리
        Toast.makeText(this, "Place saving cancelled", Toast.LENGTH_SHORT).show()
    }
}


//package com.example.myapplication.view.activity
//
//import Tab2Fragment
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import com.example.myapplication.databinding.ActivityMainBinding
//import com.example.myapplication.model.interfaces.SavePlaceListener
//import com.example.myapplication.view.fragment.Tab1Fragment
//
//import com.example.myapplication.view.fragment.Tab3Fragment
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayoutMediator
//
//class MainActivity : AppCompatActivity(), SavePlaceListener {
//    // Binding class for view access
//    private lateinit var binding: ActivityMainBinding
//
//    // Prefix and suffix text
//    private val commonPrefix = "내가 좋아하는"
//    private val commonSuffix = "만 따로 모아봐요."
//
//    // Variable parts of the text
//    private val tabMiddleDescriptions = arrayOf(
//        "/자주 연락하는 사람들의 전화번호",
//        " 사진들",
//        " 장소들"
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        // Initialize and set ViewPager2
//        binding.viewPager.adapter = TabPagerAdapter(this)
//        binding.viewPager.isUserInputEnabled = false
//
//        // Connect TabLayout and ViewPager2
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            when (position) {
//                0 -> tab.text = "전화번호부"
//                1 -> tab.text = "갤러리"
//                2 -> tab.text = "장소"
//            }
//        }.attach()
//
//        // Set initial description
//        updateDescription(0)
//
//        // Add Tab selected listener
//        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                updateDescription(tab.position)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                // Do nothing
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                // Do nothing
//            }
//        })
//    }
//
//    // Update description text based on selected tab
//    private fun updateDescription(position: Int) {
//        if (position in tabMiddleDescriptions.indices) {
//            val description = "$commonPrefix${tabMiddleDescriptions[position]}$commonSuffix"
//            binding.textDescription.text = description
//        } else {
//            binding.textDescription.text = " 것들"
//        }
//    }
//
//    private inner class TabPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
//        override fun getItemCount(): Int {
//            return tabMiddleDescriptions.size
//        }
//
//        override fun createFragment(position: Int): Fragment {
//            return when (position) {
//                0 -> Tab1Fragment()
//                1 -> Tab2Fragment()
//                2 -> Tab3Fragment()
//                else -> throw IllegalArgumentException("Invalid position: $position")
//            }
//        }
//    }
//
//    // Implement SavePlaceListener methods
//    override fun onSavePlaceClicked(placeName: String?) {
//        // Handle place saving logic
//        placeName?.let {
//            // Show a toast message or update UI
//            Toast.makeText(this, "Place saved: $placeName", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onCancelClicked() {
//        // Handle cancel logic
//        Toast.makeText(this, "Place saving cancelled", Toast.LENGTH_SHORT).show()
//    }
//}
