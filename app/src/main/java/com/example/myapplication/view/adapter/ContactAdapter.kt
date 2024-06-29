// ContactAdapter.kt
package com.example.myapplication.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.data.Contact
import com.example.myapplication.databinding.ContactListBinding
import com.example.myapplication.model.repository.ContactRepository

class ContactAdapter(
    private val context: Context,
    private var contactList: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ContactListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.nameView.text = contact.name
            binding.numberView.text = contact.phoneNumber
            binding.starView.text = if (contact.isFavorite == true) "★" else "☆"
            Glide.with(context)
                .load(contact.photoUri)
                .placeholder(R.drawable.default_profile_img) // 로딩 중 표시할 이미지
                .error(R.drawable.default_profile_img)       // 오류 시 표시할 이미지
                .into(binding.profileImageView)

            itemView.setOnClickListener {
                onItemClick.invoke(contact)
            }
            binding.starView.setOnClickListener {
                val action = if (contact.isFavorite == true) "삭제" else "추가"
                val message = "즐겨찾기를 $action 하시겠습니까?\n실제 전화번호부에도 즐겨찾기 변경 사항이 반영됩니다."

                // AlertDialog Builder를 사용하여 다이얼로그 생성
                val builder = AlertDialog.Builder(context)
                builder.setMessage(message)
                    .setCancelable(false) // 다이얼로그 바깥 클릭이나 뒤로 가기 버튼을 눌러도 다이얼로그가 닫히지 않도록 설정
                    .setPositiveButton("예") { dialog, id ->
                        // "예" 버튼을 눌렀을 때 수행할 작업
                        // 예: 즐겨찾기 해제 로직
                        ContactRepository.toggleFavoriteStatus(context, contact)
                        Toast.makeText(context, "즐겨찾기 $action 되었습니다.", Toast.LENGTH_SHORT).show()
                        // UI 업데이트를 위해 데이터 변경 후 전체 데이터 다시 불러오기
                        contactList = ContactRepository.getAllContacts()
                        notifyDataSetChanged()
                    }
                    .setNegativeButton("아니요") { dialog, id ->
                        // "아니요" 버튼을 눌렀을 때 다이얼로그를 단순히 닫음
                        dialog.dismiss()
                    }

                // 다이얼로그를 실제로 보여줌
                val alert = builder.create()
                alert.show()
            }

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

    fun updateContacts(newContacts: List<Contact>) {
        contactList = newContacts
        notifyDataSetChanged()
    }
}
