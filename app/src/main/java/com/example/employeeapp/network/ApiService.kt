package com.example.employeeapp.network

import com.example.employeeapp.UserProfile
import com.example.employeeapp.EventDetails
import com.example.employeeapp.UserCredentials
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    // Auth Endpoints
    @POST("/auth/register")
    fun registerUser(@Body credentials: UserCredentials): Call<String>

    @POST("/auth/login")
    fun loginUser(@Body credentials: UserCredentials): Call<String>

    // User Profile Endpoints
    @POST("/profile/add")
    fun addUserProfile(@Body profile: UserProfile): Call<String>

    @GET("/profile")
    fun getUserProfile(@Query("fullName") fullName: String): Call<UserProfile>

    @PUT("/profile/update")
    fun updateUserProfile(@Body profile: UserProfile): Call<String>

    // Event Endpoints
    @POST("/events/create")
    fun createEvent(@Body eventDetails: EventDetails): Call<String>
}
