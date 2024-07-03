


package com.example.myapplication.view.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.Fragment1RecyclerViewBinding
import com.example.myapplication.model.data.Contact
import com.example.myapplication.model.repository.ContactRepository
import com.example.myapplication.model.interfaces.RefreshFavoriteContactsListener
import com.example.myapplication.view.adapter.ContactAdapter
import com.example.myapplication.view.fragment.Tab1Fragment

class ContactAllActivity : AppCompatActivity() {

    private lateinit var binding: Fragment1RecyclerViewBinding
    private lateinit var adapter: ContactAdapter
    private var contacts: List<Contact> = ContactRepository.getAllContacts()
    private var tab1Fragment: Tab1Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Fragment1RecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.visibility = View.GONE

        // RecyclerView 설정
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(this, contacts, null, null, false)
        binding.contactRecyclerView.adapter = adapter

        // 검색 기능 설정
        val autoCompleteTextView = binding.address
        val searchButton = binding.submit

        searchButton.setOnClickListener {
            val query = autoCompleteTextView.text.toString().trim()
            filterContacts(query)
        }

        binding.buttonAllView.visibility = View.GONE

        tab1Fragment = supportFragmentManager.findFragmentByTag("tab1_fragment_tag") as? Tab1Fragment
    }

    private fun filterContacts(query: String) {
        val filteredContacts = contacts.filter { contact ->
            contact.name.contains(query, ignoreCase = true)
        }
        adapter.updateData(filteredContacts)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        notifyTab1Fragment()
    }

    private fun notifyTab1Fragment() {
        val tab1Fragment = supportFragmentManager.findFragmentByTag("tab1_fragment_tag") as? Tab1Fragment
        tab1Fragment?.refreshFavoriteContacts()
    }

    override fun onDestroy() {
        super.onDestroy()
        notifyTab1Fragment()
    }
}
