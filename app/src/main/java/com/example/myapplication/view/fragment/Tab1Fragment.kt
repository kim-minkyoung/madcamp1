package com.example.myapplication.view.fragment

import ContactViewModel
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.view.adapter.ContactAdapter
import com.example.myapplication.model.repository.ContactRepository
import com.example.myapplication.databinding.Fragment1RecyclerViewBinding
import com.example.myapplication.view.activity.ContactAllActivity
import com.example.myapplication.view.activity.ContactDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var _binding: Fragment1RecyclerViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactViewModel: ContactViewModel

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
        _binding = Fragment1RecyclerViewBinding.inflate(inflater, container, false)
        binding.toolbar.visibility = View.GONE

        // ViewModel 초기화
        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)

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
            observeFavoriteContacts()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAllView.setOnClickListener {
            // 버튼을 클릭했을 때 실행될 로직
            navigateToViewAllContact()
        }
    }

    private fun navigateToViewAllContact() {
        val intent = Intent(activity, ContactAllActivity::class.java)
        startActivity(intent)
    }

    private fun observeFavoriteContacts() {
        // LiveData를 관찰하여 데이터가 업데이트될 때마다 UI 갱신
        contactViewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            updateRecyclerView(contacts)
        }
    }

    private fun readContacts() {
        // 코루틴을 사용하여 백그라운드에서 데이터 쿼리
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                ContactRepository.loadAllContacts(requireContext())
                val contacts = ContactRepository.getFavoriteContacts()
                withContext(Dispatchers.Main) {
                    updateRecyclerView(contacts)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        // Refresh contacts when the fragment resumes
//        readContacts()
//    }

    private fun updateRecyclerView(contacts: List<com.example.myapplication.model.data.Contact>) {
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
                    observeFavoriteContacts()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
