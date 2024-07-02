package com.example.myapplication.model.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.data.Contact
import com.example.myapplication.model.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val contactRepository = ContactRepository
    val favoriteContacts = MutableLiveData<List<Contact>>()

    init {
        // 최초 데이터 로드
        loadAllContacts()
    }

    private fun loadAllContacts() {
        viewModelScope.launch {
            if (contactRepository.contacts.isEmpty()) {
                // 최초 한 번만 데이터 로드
                contactRepository.loadAllContacts(getApplication())
            }
            favoriteContacts.value = contactRepository.getFavoriteContacts()
        }
    }

    // 즐겨찾기 상태를 변경하는 함수
    fun toggleFavoriteStatus(contact: Contact) {
        viewModelScope.launch {
            contactRepository.toggleFavoriteStatus(getApplication(), contact)
            // 즐겨찾기 상태 변경 후 LiveData를 업데이트하여 Observer에게 알림
            favoriteContacts.value = contactRepository.getFavoriteContacts()
        }
    }

    fun loadFavoriteContacts() {
        viewModelScope.launch {
            favoriteContacts.value = contactRepository.getFavoriteContacts()
        }
    }
}
