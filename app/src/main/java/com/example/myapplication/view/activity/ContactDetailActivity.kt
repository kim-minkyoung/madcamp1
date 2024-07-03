package com.example.myapplication.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.data.Contact
import com.example.myapplication.databinding.ActivityContactDetailBinding

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactDetailBinding // Declare binding variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the Contact object from intent extras
        val contact = intent.getSerializableExtra("contact") as? Contact

        if (contact != null) {
            // Set profile image
            Glide.with(this)
                .load(contact.photoUri)
                .placeholder(R.drawable.default_profile_img)
                .error(R.drawable.default_profile_img)
                .into(binding.profileImageView)

            // Set name
            binding.nameTextView.text = contact.name

            // Set phone number
            binding.phoneTextView.text = contact.phoneNumber ?: ""
        } else {
            Toast.makeText(this, "Contact information not available", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
