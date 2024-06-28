package com.example.myapplication.model.repository

import android.content.Context
import android.provider.ContactsContract
import com.example.myapplication.model.data.Contact


object ContactRepository {
    val contacts = mutableListOf<Contact>()
    val contactsFavorite = mutableListOf<Contact>()

//    fun addContact(contact: Contact) {
//        contacts.add(contact)
//    }

    fun loadAllContacts(context: Context) {
        val contactsUri = ContactsContract.Contacts.CONTENT_URI
        val cursor = context.contentResolver.query(
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
                    val phoneCursor = context.contentResolver.query(
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
                    val photoCursor = context.contentResolver.query(
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

                val contact = Contact(
                    name,
                    formattedPhoneNumber,
                    photoUri,
                    isFavorite
                )

                contacts.add(contact)

                if (isFavorite) {
                    contactsFavorite.add(contact)
                }
            }
        }

    }

    fun getContactByIndex(index: Int): Contact? {
        return if (index in contacts.indices) contacts[index] else null
    }

    fun getAllContacts(): List<Contact> {
        return contacts.toList()
    }

    fun getFavoriteContacts(): List<Contact> {
        return contactsFavorite.toList()
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
}
