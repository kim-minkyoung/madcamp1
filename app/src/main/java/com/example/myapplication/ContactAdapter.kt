// ContactAdapter.kt
package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ContactListBinding

class ContactAdapter(
    private val context: Context,
    private val contactList: List<Tab1Fragment.Contact>
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ContactListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Tab1Fragment.Contact) {
            binding.nameView.text = contact.name
            binding.numberView.text = contact.phoneNumber
            binding.starView.text = if (contact.isFavorite == true) "★" else "☆"
            Glide.with(context)
                .load(contact.photoUri)
                .placeholder(R.drawable.default_profile_img) // 로딩 중 표시할 이미지
                .error(R.drawable.default_profile_img)       // 오류 시 표시할 이미지
                .into(binding.profileImageView)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ContactListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}
