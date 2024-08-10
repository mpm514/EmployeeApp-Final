package com.example.employeeapp

data class UserCredentials(
    val id: String = "",
    val password: String = "",
    val isAdmin: Boolean = false // Add this line
)
