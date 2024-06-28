package com.example.myapplication.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.view.adapter.ContactAdapter
import com.example.myapplication.model.data.Contact
import com.example.myapplication.model.repository.ContactRepository
import com.example.myapplication.databinding.Fragment1RecyclerViewBinding

class ContactAllActivity : AppCompatActivity() {
    private lateinit var binding: Fragment1RecyclerViewBinding // Activity의 바인딩 클래스로 수정
    private var contacts: List<Contact> = ContactRepository.getAllContacts()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Fragment1RecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) // 사용할 툴바 설정
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // 뒤로가기 버튼 활성화
            setHomeAsUpIndicator(android.R.drawable.ic_menu_revert) // 기본 뒤로가기 아이콘 설정
        }

        // RecyclerView 설정
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ContactAdapter(this, contacts) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            startActivity(intent)
        }
        binding.contactRecyclerView.adapter = adapter

        binding.buttonAllView.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
