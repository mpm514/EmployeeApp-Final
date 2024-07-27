package com.example.employeeapp.repository

import com.example.employeeapp.UserProfile
import com.example.employeeapp.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call

class UserProfileRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://yourbackendurl.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun addUserProfile(profile: UserProfile): Call<String> {
        return apiService.addUserProfile(profile)
    }

    fun getUserProfile(fullName: String): Call<UserProfile> {
        return apiService.getUserProfile(fullName)
    }

    fun updateUserProfile(profile: UserProfile): Call<String> {
        return apiService.updateUserProfile(profile)
    }
}
