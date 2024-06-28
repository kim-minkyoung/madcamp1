package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentTab1Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var _binding: FragmentTab1Binding? = null
    private val binding get() = _binding!!

    data class Contact(
        val name: String,
        val phoneNumber: String?,
        val photoUri: String?,
        val isFavorite: Boolean?
    )
    val contactList = mutableListOf<Contact>()

    companion object {
        private const val REQUEST_READ_CONTACTS = 101
        private const val REQUEST_WRITE_CONTACTS = 102
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
                Manifest.permission.READ_CONTACTS,
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
        val contactsUri = ContactsContract.Contacts.CONTENT_URI
        // 코루틴을 사용하여 백그라운드에서 데이터 쿼리
        lifecycleScope.launch(Dispatchers.IO) {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.STARRED
            )

            try {
                val cursor = requireContext().contentResolver.query(
                    contactsUri,
                    null,
                    null,
                    null,
                    null
                )

                cursor?.use { c ->
                    val idIndex = c.getColumnIndex(ContactsContract.Contacts._ID)
                    val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val starredIndex = c.getColumnIndex(ContactsContract.Contacts.STARRED)

                    while (c.moveToNext()) {
                        val contactId = if (idIndex != -1) c.getString(idIndex) else null
                        val name = if (nameIndex != -1) c.getString(nameIndex) ?: "Unknown" else "Unknown"
                        val isFavorite = if (starredIndex != -1) c.getInt(starredIndex) == 1 else false

                        val phoneNumber: String? = contactId?.let { id ->
                            val phoneCursor = requireContext().contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                                "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                                arrayOf(id),
                                null
                            )

                            phoneCursor?.use { pc ->
                                if (pc.moveToNext()) {
                                    val numberIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                    if (numberIndex != -1) pc.getString(numberIndex) else null
                                } else {
                                    null
                                }
                            }
                        }

                        val formattedPhoneNumber = phoneNumber?.let { formatPhoneNumber(it) } ?: "000-0000-0000"

                        val photoUri: String? = contactId?.let { id ->
                            val photoCursor = requireContext().contentResolver.query(
                                ContactsContract.Data.CONTENT_URI,
                                arrayOf(ContactsContract.CommonDataKinds.Photo.PHOTO_URI),
                                "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                                arrayOf(id, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE),
                                null
                            )

                            photoCursor?.use { pc ->
                                if (pc.moveToNext()) {
                                    val photoIndex = pc.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI)
                                    if (photoIndex != -1) pc.getString(photoIndex) else null
                                } else {
                                    null
                                }
                            }
                        }

                        // 백그라운드에서 처리 중인 경우에만 contactList에 추가
                        contactList.add(Contact(name, formattedPhoneNumber, photoUri, isFavorite))
                    }
                }

                // 데이터 처리 후 메인 스레드에서 RecyclerView 업데이트
                withContext(Dispatchers.Main) {
                    updateRecyclerView()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying contacts", e)
            }
        }


    }

    // 전화번호를 원하는 형식으로 변환하는 함수
    private fun formatPhoneNumber(phoneNumber: String?): String {
        // 전화번호가 null인 경우 빈 문자열을 반환
        if (phoneNumber.isNullOrEmpty()) return ""

        // 전화번호에서 숫자만 추출
        val digits = phoneNumber.filter { it.isDigit() }

        // 전화번호 길이 확인
        if (digits.length != 11) return phoneNumber // 너무 짧은 경우 원래 형식 유지

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
