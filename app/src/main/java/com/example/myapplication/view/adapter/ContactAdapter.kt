package com.example.myapplication.view.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.data.Contact
import com.example.myapplication.databinding.ContactListBinding
import com.example.myapplication.model.repository.ContactRepository
import com.example.myapplication.model.viewModel.ContactViewModel
import com.example.myapplication.view.activity.ContactDetailActivity
import com.example.myapplication.view.fragment.Tab1Fragment

class ContactAdapter(
    private val context: Context,
    private var contactList: List<Contact>,
    private val lifecycleOwner: LifecycleOwner?,
    private val contactViewModel: ContactViewModel?,
    private val isTab1Fragment: Boolean,
    private val onItemClick: (Contact) -> Unit = { contact ->
        val intent = Intent(context, ContactDetailActivity::class.java)
        context.startActivity(intent)
    }
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private var filteredContacts: List<Contact> = contactList
    private var favoriteContacts: MutableLiveData<List<Contact>> =
        contactViewModel?.favoriteContacts ?: MutableLiveData(emptyList())

    inner class ViewHolder(private val binding: ContactListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.nameView.text = contact.name
            binding.numberView.text = contact.phoneNumber
            binding.starView.text = if (contact.isFavorite == true) "★" else "☆"
            Glide.with(context)
                .load(contact.photoUri)
                .placeholder(R.drawable.default_profile_img)
                .error(R.drawable.default_profile_img)
                .into(binding.profileImageView)

            itemView.setOnClickListener {
                onItemClick.invoke(contact)
            }
            binding.starView.setOnClickListener {
                val action = if (contact.isFavorite == true) "삭제" else "추가"
                val message = "즐겨찾기를 $action 하시겠어요?\n실제 전화번호부에도 즐겨찾기 변경 사항이 반영 돼요."

                val builder = AlertDialog.Builder(context)
                builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("예") { dialog, id ->
                        if (contactViewModel != null) {
                            contactViewModel.toggleFavoriteStatus(contact)
                            favoriteContacts.value = ContactRepository.getFavoriteContacts()
                            contactList = ContactRepository.getFavoriteContacts()
                            filterContacts("")
                            Log.d(TAG, "즐겨찾기만 보기 화면")
                        } else {
                            ContactRepository.toggleFavoriteStatus(context, contact)
                            // UI 업데이트를 위해 데이터 변경 후 전체 데이터 다시 불러오기
                            favoriteContacts.value = ContactRepository.getFavoriteContacts()
                            contactList = ContactRepository.getAllContacts()
                            filterContacts("")
                            Log.d(TAG, "모두 보기 화면")
                        }
                        Toast.makeText(context, "즐겨찾기 $action 되었습니다.", Toast.LENGTH_SHORT).show()
                        notifyDataSetChanged()
                    }
                    .setNegativeButton("아니요") { dialog, id ->
                        dialog.dismiss()
                    }

                val alert = builder.create()
                alert.show()
            }
            binding.starView.setShadowLayer(10f, 5f, 5f, Color.GRAY);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ContactListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = filteredContacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return filteredContacts.size
    }

    fun updateData(newContacts: List<Contact>) {
        contactList = newContacts
        filterContacts("")
        notifyDataSetChanged()
    }

    private fun filterContacts(query: String) {
        // 필터링 조건에 맞는 아이템만 남기기
        filteredContacts = contactList.filter { contact ->
            val matchesQuery = contact.name.contains(query, ignoreCase = true)
            val matchesFavorite = if (isTab1Fragment) contact.isFavorite else true
            matchesQuery && matchesFavorite == true
        }
        notifyDataSetChanged()
    }
}
