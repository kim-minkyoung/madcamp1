package com.example.myapplication.model.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BottomSheetViewModel : ViewModel() {

    private val _addressList = MutableLiveData<List<String>>()
    val addressList: LiveData<List<String>> = _addressList

    private val _isEmptyState = MutableLiveData<Boolean>()
    val isEmptyState: LiveData<Boolean> = _isEmptyState

    init {
        _addressList.value = emptyList()
        updateEmptyState()
    }

    fun addAddress(address: String) {
        val currentList = _addressList.value?.toMutableList() ?: mutableListOf()
        currentList.add(address)
        _addressList.value = currentList
        updateEmptyState()
    }

    fun clearAddresses() {
        _addressList.value = emptyList()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        _isEmptyState.value = _addressList.value.isNullOrEmpty()
    }
}
