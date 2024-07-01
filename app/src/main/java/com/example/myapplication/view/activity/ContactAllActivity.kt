package com.example.myapplication.view.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.Fragment1RecyclerViewBinding
import com.example.myapplication.model.data.Contact
import com.example.myapplication.model.repository.ContactRepository
import com.example.myapplication.model.interfaces.RefreshFavoriteContactsListener
import com.example.myapplication.view.adapter.ContactAdapter
import com.example.myapplication.view.fragment.Tab1Fragment

class ContactAllActivity : AppCompatActivity(), RefreshFavoriteContactsListener {
    private lateinit var binding: Fragment1RecyclerViewBinding
    private var contacts: List<Contact> = ContactRepository.getAllContacts()
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Fragment1RecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.visibility = View.GONE

        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter 초기화
        adapter = ContactAdapter(this, contacts, null, null) { contact ->
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

    // 즐겨찾기 상태 변경 후 Tab1Fragment에 알리기
    override fun onBackPressed() {
        super.onBackPressed()
        notifyTab1Fragment()
    }

    // Tab1Fragment로 즐겨찾기 목록 새로고침 이벤트 전달
    override fun onRefreshFavoriteContacts() {
        val fragmentTag = "tab1_fragment_tag"
        val fragment = supportFragmentManager.findFragmentByTag(fragmentTag) as? Tab1Fragment
        if (fragment != null && fragment.isAdded) {
            Log.d(TAG, "Calling refreshFavoriteContacts()")
            fragment.refreshFavoriteContacts()
        } else {
            Log.d(TAG, "Fragment with tag $fragmentTag not found or not added")
        }
    }



    // Tab1Fragment로 즐겨찾기 목록 새로고침 요청
    private fun notifyTab1Fragment() {
        onRefreshFavoriteContacts()
    }
}
