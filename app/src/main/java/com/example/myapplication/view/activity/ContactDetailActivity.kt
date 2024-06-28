package com.example.myapplication.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.view.fragment.Tab1Fragment
import com.example.myapplication.model.repository.ContactRepository
import com.example.myapplication.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val contacts = ContactRepository.getAllContacts()

        // Check if contacts is not null and is not empty
        if (!contacts.isNullOrEmpty()) {
            // Assume you want to display the first contact from the list
            val firstContact = contacts[0]

            // Display the contact information in your views
            binding.nameView.text = firstContact.name
            binding.numberView.text = firstContact.phoneNumber
        } else {
            // Handle case where contacts is null or empty
            // For example, show an error message or take appropriate action
            // It's good practice to handle this case to avoid crashes
            Toast.makeText(this, "No contacts found", Toast.LENGTH_SHORT).show()
        }
        // 추가적인 정보 표시 등 필요한 작업 수행
    }

    companion object {
        fun newIntent(context: Context, contact: Tab1Fragment.Contact): Intent {
            val intent = Intent(context, ContactDetailActivity::class.java)
            return intent
        }
    }
}
