package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentTab1Binding

class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var _binding: FragmentTab1Binding? = null
    private val binding get() = _binding!!

    data class Contact(
        val name: String,
        val phoneNumber: String?,
        val photoUri: String?
    )
    val contactList = mutableListOf<Contact>()

    companion object {
        private const val REQUEST_READ_CONTACTS = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTab1Binding.inflate(inflater, container, false)
        val view = binding.root

        // READ_CONTACTS 권한 확인 및 처리
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        } else {
            // 권한이 있는 경우 연락처 데이터 읽어오기
            readContacts()
        }

        return view
    }

    @SuppressLint("Range")
    private fun readContacts() {
        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val name =
                    c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber =
                    c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                val photoUri =
                    c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))

                contactList.add(Contact(name, formattedPhoneNumber, photoUri))
            }
        }

        updateRecyclerView()
    }

    // 전화번호를 원하는 형식으로 변환하는 함수
    private fun formatPhoneNumber(phoneNumber: String?): String {
        // 전화번호가 null인 경우 빈 문자열을 반환
        if (phoneNumber.isNullOrEmpty()) return ""

        // 전화번호에서 숫자만 추출
        val digits = phoneNumber.filter { it.isDigit() }

        // 전화번호 길이 확인
        if (digits.length < 10) return phoneNumber // 너무 짧은 경우 원래 형식 유지

        // 형식화된 전화번호 생성
        return "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts()
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
    }

    private fun updateRecyclerView() {
        recyclerView = binding.contactRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Use requireContext() here
        contactAdapter = ContactAdapter(requireContext(), contactList) // Use requireContext() here
        recyclerView.adapter = contactAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
