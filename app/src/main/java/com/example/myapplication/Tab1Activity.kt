
package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.databinding.ActivytyTab1Binding

class Tab1Activity : AppCompatActivity() {

    private lateinit var binding: ActivytyTab1Binding
    private lateinit var adapter: ArrayAdapter<String>

    companion object {
        private const val REQUEST_READ_CONTACTS = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivytyTab1Binding.inflate(layoutInflater) // ViewBinding 초기화
        val view = binding.root
        setContentView(view)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        binding.contactListView.adapter = adapter

        // READ_CONTACTS 권한 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없는 경우 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        } else {
            // 권한이 있는 경우 연락처 데이터 읽어오기
            readContacts()
        }
    }

    @SuppressLint("Range")
    private fun readContacts() {
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.use { c ->
            while (c.moveToNext()) {
                val name =
                    c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
//                val number = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                adapter.add(name)
            }
        }
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

}
