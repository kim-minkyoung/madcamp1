package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tab 1"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tab 2"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tab 3"))

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // TODO
                        startActivity(Intent(this@MainActivity, Tab1Activity::class.java))
                    }
                    1 -> {
                        // TODO
                    }
                    2 -> {
                        // TODO
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Implement if needed
                startActivity(Intent(this@MainActivity, Tab1Activity::class.java))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Implement if needed
            }
        })
    }
}
