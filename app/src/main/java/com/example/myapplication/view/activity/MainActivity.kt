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

// MainActivity 클래스는 AppCompatActivity를 상속받아 앱의 메인 화면을 관리합니다.
class MainActivity : AppCompatActivity(), SavePlaceListener {
    private lateinit var binding: ActivityMainBinding

    // onCreate 메서드는 액티비티가 생성될 때 호출됩니다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding을 사용하여 XML 레이아웃 파일을 인플레이트합니다.
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // ViewPager2 초기화 및 설정
        binding.viewPager.adapter = TabPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false

        // TabLayout과 ViewPager2를 연결합니다.
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "전화번호부"
                1 -> tab.text = "갤러리"
                2 -> tab.text = "장소"
            }
        }.attach()

        // 초기 설명을 설정합니다.
        updateDescription(0)

        // 탭 선택 리스너를 추가합니다.
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

    // 선택된 탭에 따라 설명을 업데이트하는 메서드입니다.
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

    // TabPagerAdapter 클래스는 ViewPager2를 위한 어댑터입니다.
    private inner class TabPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        // 선택된 포지션에 따라 적절한 프래그먼트를 반환합니다.
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Tab1Fragment()
                1 -> Tab2Fragment()
                2 -> Tab3Fragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }

    // SavePlaceListener 인터페이스의 onSavePlaceClicked 메서드 구현입니다.
    override fun onSavePlaceClicked(address: String?) {
        address?.let {
            // Tab3Fragment를 찾아서 장소를 저장합니다.
            val tab3Fragment = supportFragmentManager.fragments.find { it is Tab3Fragment } as? Tab3Fragment
            tab3Fragment?.updateBottomSheet(it, tab3Fragment.marker.position.latitude, tab3Fragment.marker.position.longitude)
            Toast.makeText(this, "장소가 저장 됐어요: $address", Toast.LENGTH_SHORT).show()
        }
    }

    // SavePlaceListener 인터페이스의 onCancelClicked 메서드 구현입니다.
    override fun onCancelClicked() {
        Toast.makeText(this, "장소 저장이 취소 됐어요", Toast.LENGTH_SHORT).show()
    }
}
