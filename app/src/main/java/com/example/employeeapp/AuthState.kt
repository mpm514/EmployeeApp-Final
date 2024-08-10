package com.example.employeeapp

sealed class AuthState {
    data class Authenticated(val email: String) : AuthState() // Authenticated now takes an email
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}




