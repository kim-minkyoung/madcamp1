package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
    private val contactList = mutableListOf<Contact>()

    companion object {
        private const val REQUEST_READ_CONTACTS = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTab1Binding.inflate(inflater, container, false)
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
        return binding.root
    }

    private fun readContacts() {
        // 코루틴을 사용하여 백그라운드에서 데이터 쿼리
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val contacts = ContactRepository.loadAllContacts(requireContext())
                withContext(Dispatchers.Main) {
                    updateRecyclerView(contacts)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateRecyclerView(contacts: List<com.example.myapplication.Contact>) {
        recyclerView = binding.contactRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter 초기화 및 설정
        contactAdapter = ContactAdapter(requireContext(), contacts)  { contact ->
            // 클릭 이벤트 처리
            val intent = Intent(activity, ContactDetailActivity::class.java)
            startActivity(intent)
        }
        recyclerView.adapter = contactAdapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
