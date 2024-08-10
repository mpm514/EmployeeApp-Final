package com.example.employeeapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class AuthViewModel(private val context: Context) : ViewModel() {

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail

    init {
        signout()
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        _authState.value = AuthState.Unauthenticated
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password cannot be null")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userIsAdmin = validateUserCredentials(context, email, password)
                if (userIsAdmin != null) {
                    _userEmail.postValue(email)
                    _authState.postValue(AuthState.Authenticated(email)) // Pass email to Authenticated state
                } else {
                    _authState.postValue(AuthState.Error("Invalid credentials"))
                }
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message ?: "Oops! Something went wrong"))
            }
        }
    }

    fun signup(email: String, password: String, isAdmin: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password cannot be null")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userExists = checkIfUserExists(context, email)
                if (userExists) {
                    _authState.postValue(AuthState.Error("User already exists. Please log in."))
                } else {
                    saveUserToCsv(context, email, password, isAdmin)
                    _userEmail.postValue(email)
                    _authState.postValue(AuthState.Authenticated(email)) // Pass email to Authenticated state
                    _message.postValue("Account created successfully. Please log in.")
                }
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message ?: "Oops! Something went wrong"))
            }
        }
    }

    fun signout() {
        _authState.value = AuthState.Unauthenticated
        _userEmail.value = ""  // Clear the stored email on signout
    }

    fun setMessage(message: String) {
        _message.value = message
    }

    // New function without password
    fun validateUserCredentials(context: Context, email: String, password:String? = null): Boolean? {
        val fileName = "users.csv"
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return null

        try {
            val reader = BufferedReader(FileReader(file))
            reader.useLines { lines ->
                lines.forEach { line ->
                    val data = line.split(",")
                    if (data.size >= 3 && data[0] == email) {
                        if (password != null) {
                            if (data[1] == password) {
                                Log.d("AuthViewModel", "User found: $email with role: ${data[2]}")
                                return data[2] == "admin"
                            }
                        } else {
                            Log.d("AuthViewModel", "User found: $email with role: ${data[2]}")
                            return data[2] == "admin"
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("AuthViewModel", "User not found: $email")
        return null
    }


    private fun checkIfUserExists(context: Context, email: String): Boolean {
        val fileName = "users.csv"
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return false

        try {
            val reader = BufferedReader(FileReader(file))
            reader.useLines { lines ->
                lines.forEach { line ->
                    val data = line.split(",")

                    Log.d("AuthViewModel", "Read line: $line")

                    if (data.size >= 1 && data[0] == email) {
                        Log.d("AuthViewModel", "User already exists: $email")
                        return true
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d("AuthViewModel", "User does not exist: $email")
        return false
    }

    private fun saveUserToCsv(context: Context, email: String, password: String, isAdmin: Boolean) {
        val fileName = "users.csv"
        val file = File(context.filesDir, fileName)
        try {
            val writer = FileWriter(file, true)
            val adminFlag = if (isAdmin) "admin" else "user"
            writer.write("$email,$password,$adminFlag\n")
            writer.close()
            Log.d("AuthViewModel", "User saved: $email")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
