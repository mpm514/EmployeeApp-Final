package com.example.employeeapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.employeeapp.UserCredentials
import com.example.employeeapp.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import retrofit2.await

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    init {
        signout()
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        // Implement logic to check authentication status if needed
        _authState.value = AuthState.Unauthenticated
    }

    fun login(email: String, password: String) = liveData(Dispatchers.IO) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.postValue(AuthState.Error("Email or password cannot be null"))
            return@liveData
        }
        _authState.postValue(AuthState.Loading)
        try {
            val response = authRepository.loginUser(UserCredentials(email, password)).await()
            _authState.postValue(AuthState.Authenticated)
            emit(response)
        } catch (e: Exception) {
            _authState.postValue(AuthState.Error(e.message ?: "Oops! Something went wrong"))
            emit(e.message)
        }
    }

    fun signup(email: String, password: String) = liveData(Dispatchers.IO) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.postValue(AuthState.Error("Email or password cannot be null"))
            return@liveData
        }
        _authState.postValue(AuthState.Loading)
        try {
            val response = authRepository.registerUser(UserCredentials(email, password)).await()
            _authState.postValue(AuthState.Authenticated)
            emit(response)
        } catch (e: Exception) {
            _authState.postValue(AuthState.Error(e.message ?: "Oops! Something went wrong"))
            emit(e.message)
        }
    }

    fun signout() {
        // Implement signout logic if needed
        _authState.value = AuthState.Unauthenticated
    }

    fun setMessage(message: String) {
        _message.value = message
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
