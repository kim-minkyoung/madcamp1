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

class ContactAllActivity : AppCompatActivity(), RefreshFavoriteContactsListener {

    private lateinit var binding: Fragment1RecyclerViewBinding
    private lateinit var adapter: ContactAdapter
    private var contacts: List<Contact> = ContactRepository.getAllContacts()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Fragment1RecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.visibility = View.GONE

        // RecyclerView 설정
        binding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter(this, contacts, null, null) { contact ->
            val intent = Intent(this, ContactDetailActivity::class.java)
            startActivity(intent)
        }
        binding.contactRecyclerView.adapter = adapter

        // 검색 기능 설정
        val autoCompleteTextView = binding.address
        val searchButton = binding.submit

        searchButton.setOnClickListener {
            val query = autoCompleteTextView.text.toString().trim()
            filterContacts(query)
        }

        binding.buttonAllView.visibility = View.GONE
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
        onRefreshFavoriteContacts()
    }

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

    private fun notifyTab1Fragment() {
        onRefreshFavoriteContacts()
    }
}
