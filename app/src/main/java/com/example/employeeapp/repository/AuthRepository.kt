package com.example.employeeapp.repository

import com.example.employeeapp.UserCredentials
import com.example.employeeapp.network.ApiService
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun registerUser(credentials: UserCredentials): Call<String> {
        return apiService.registerUser(credentials)
    }

    fun loginUser(credentials: UserCredentials): Call<String> {
        return apiService.loginUser(credentials)
    }
}
