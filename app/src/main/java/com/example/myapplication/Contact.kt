package com.example.myapplication

import java.io.Serializable

data class Contact(
    val name: String,
    val phoneNumber: String?,
    val photoUri: String?,
    val isFavorite: Boolean?
) : Serializable
