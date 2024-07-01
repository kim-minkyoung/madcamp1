package com.example.myapplication.view.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.Fragment1RecyclerViewBinding
import com.example.myapplication.model.data.Contact
import com.example.myapplication.view.activity.ContactAllActivity
import com.example.myapplication.view.activity.ContactDetailActivity
import com.example.myapplication.view.adapter.ContactAdapter
import com.example.myapplication.model.viewModel.ContactViewModel

class Tab1Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private var _binding: Fragment1RecyclerViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactViewModel: ContactViewModel

    companion object {
        private const val REQUEST_PERMISSIONS_CONTACTS = 101
        private const val TAG = "Tab1Fragment"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach() called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView() called")

        _binding = Fragment1RecyclerViewBinding.inflate(inflater, container, false)
        binding.toolbar.visibility = View.GONE

        // ViewModel 초기화
        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)

        // 연락처 권한 확인 및 처리
        checkContactPermissions()

        // 검색 버튼 클릭 이벤트 처리
        binding.submit.setOnClickListener {
            val query = binding.address.text.toString().trim()
            observeFavoriteContacts(query)
        }

        // 전체 연락처 보기 버튼 클릭 시 이벤트 처리
        binding.buttonAllView.setOnClickListener {
            navigateToViewAllContact()
        }

        return binding.root
    }

    private fun checkContactPermissions() {
        val readPermission = Manifest.permission.READ_CONTACTS
        val writePermission = Manifest.permission.WRITE_CONTACTS

        val readPermissionGranted = ContextCompat.checkSelfPermission(requireContext(), readPermission) == PackageManager.PERMISSION_GRANTED
        val writePermissionGranted = ContextCompat.checkSelfPermission(requireContext(), writePermission) == PackageManager.PERMISSION_GRANTED

        if (!readPermissionGranted || !writePermissionGranted) {
            // 권한이 없는 경우 권한 요청
            val permissionsToRequest = mutableListOf<String>()
            if (!readPermissionGranted) permissionsToRequest.add(readPermission)
            if (!writePermissionGranted) permissionsToRequest.add(writePermission)

            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_CONTACTS
            )
        } else {
            // 권한이 모두 있는 경우 연락처 데이터 읽어오기
            observeFavoriteContacts("")
        }
    }

    private fun observeFavoriteContacts(query: String) {
        // LiveData를 관찰하여 데이터가 업데이트될 때마다 UI 갱신
        contactViewModel.favoriteContacts.observe(viewLifecycleOwner) { contacts ->
            updateRecyclerView(contacts, query)
        }
    }

    private fun updateRecyclerView(contacts: List<Contact>, query: String) {
        recyclerView = binding.contactRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 검색 쿼리에 따라 연락처 필터링
        val filteredContacts = if (query.isBlank()) {
            contacts // 필터 없음
        } else {
            contacts.filter { contact ->
                contact.name.contains(query, ignoreCase = true)
            }
        }

        // Adapter 초기화 및 설정
        contactAdapter = ContactAdapter(requireContext(), filteredContacts, viewLifecycleOwner, contactViewModel) { contact ->
            // 클릭 이벤트 처리
            val intent = Intent(requireContext(), ContactDetailActivity::class.java)
            startActivity(intent)
        }
        recyclerView.adapter = contactAdapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // 모든 권한이 허용된 경우 연락처 데이터 읽어오기
                    observeFavoriteContacts("")
                } else {
                    // 권한이 거부된 경우 사용자에게 앱 기능 제약 사항을 안내하는 등의 처리
                    Toast.makeText(requireContext(), "연락처 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToViewAllContact() {
        val intent = Intent(requireContext(), ContactAllActivity::class.java)
        startActivity(intent)
    }

    // 외부에서 호출하여 즐겨찾기 목록을 다시 로드하는 함수
    fun refreshFavoriteContacts() {
        Log.d(TAG, "refreshFavoriteContacts() called")
        Toast.makeText(requireContext(), "refreshFavoriteContacts.", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated() called")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated() called")
    }

    override fun onStart() {
        super.onStart()
        observeFavoriteContacts("")
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        observeFavoriteContacts("")
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")

        _binding = null
    }
}
