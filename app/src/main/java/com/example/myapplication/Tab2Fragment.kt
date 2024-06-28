//package com.example.myapplication
//
//import android.Manifest
//import android.R
//import android.annotation.SuppressLint
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.provider.ContactsContract
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//
//import com.example.myapplication.databinding.FragmentTab1Binding
//
//class Tab2Fragment: Fragment() {
//
//    private var _binding: FragmentTab1Binding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var adapter: ArrayAdapter<String>
//
//    companion object {
//        private const val REQUEST_READ_CONTACTS = 101
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentTab1Binding.inflate(inflater, container, false)
//        val view = binding.root
//
//        adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1)
//        binding.contactRecyclerView.adapter = adapter
//
//        // READ_CONTACTS 권한 확인
//        if (ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.READ_CONTACTS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // 권한이 없는 경우 권한 요청
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(Manifest.permission.READ_CONTACTS),
//                REQUEST_READ_CONTACTS
//            )
//        } else {
//            // 권한이 있는 경우 연락처 데이터 읽어오기
//            readContacts()
//        }
//
//        return view
//    }
//
//    @SuppressLint("Range")
//    private fun readContacts() {
//        val cursor = requireContext().contentResolver.query(
//            ContactsContract.Contacts.CONTENT_URI,
//            null,
//            null,
//            null,
//            null
//        )
//
//        cursor?.use { c ->
//            while (c.moveToNext()) {
//                val name =
//                    c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//                adapter.add(name)
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            REQUEST_READ_CONTACTS -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    readContacts()
//                }
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
