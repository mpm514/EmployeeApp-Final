package com.example.employeeapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.employeeapp.UserProfile
import java.io.*

class ProfileViewModel : ViewModel() {

    // LiveData fields to hold profile information
    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> get() = _fullName

    private val _address1 = MutableLiveData<String>()
    val address1: LiveData<String> get() = _address1

    private val _address2 = MutableLiveData<String>()
    val address2: LiveData<String> get() = _address2

    private val _city = MutableLiveData<String>()
    val city: LiveData<String> get() = _city

    private val _state = MutableLiveData<String>()
    val state: LiveData<String> get() = _state

    private val _zipCode = MutableLiveData<String>()
    val zipCode: LiveData<String> get() = _zipCode

    private val _skills = MutableLiveData<List<String>>()
    val skills: LiveData<List<String>> get() = _skills

    private val _preferences = MutableLiveData<String>()
    val preferences: LiveData<String> get() = _preferences

    private val _availability = MutableLiveData<String>()  // Changed to String
    val availability: LiveData<String> get() = _availability

    // Function to update profile information in LiveData
    fun updateProfile(
        fullName: String,
        address1: String,
        address2: String,
        city: String,
        state: String,
        zipCode: String,
        skills: List<String>,
        preferences: String,
        availability: List<String>
    ) {
        _fullName.value = fullName
        _address1.value = address1
        _address2.value = address2
        _city.value = city
        _state.value = state
        _zipCode.value = zipCode
        _skills.value = skills
        _preferences.value = preferences
        _availability.value = availability.joinToString(separator = "|") // Join list to a string

        Log.d("ProfileViewModel", "Profile updated for: $fullName")
    }

    fun saveUserProfile(context: Context, email: String, profile: UserProfile) {
        val fileName = "users.csv"
        val file = File(context.filesDir, fileName)
        val userProfiles = mutableListOf<String>()

        if (file.exists()) {
            try {
                val reader = BufferedReader(FileReader(file))
                reader.useLines { lines ->
                    lines.forEach { line ->
                        val data = line.split(",")
                        if (data[0] == email) {
                            // Retain the password and admin status from the existing profile
                            val password = data[1]
                            val role = data[2]
                            val updatedProfile = "$email,$password,$role,${profile.fullName},${profile.address},${profile.city},${profile.state},${profile.zipcode},${profile.skills.joinToString("|")},${profile.preferences},${profile.availability}"
                            userProfiles.add(updatedProfile)
                        } else {
                            userProfiles.add(line)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            // Handle the case where the file doesn't exist
            val newUserProfile = "$email,${profile.fullName},${profile.address},${profile.city},${profile.state},${profile.zipcode},${profile.skills.joinToString("|")},${profile.preferences},${profile.availability}"
            userProfiles.add(newUserProfile)
        }

        try {
            val writer = FileWriter(file, false)
            userProfiles.forEach { profileLine ->
                writer.write("$profileLine\n")
            }
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    // Function to load a user profile from a CSV file
    fun loadUserProfileByEmail(context: Context, email: String): UserProfile? {
        val fileName = "users.csv"
        val file = File(context.filesDir, fileName)

        Log.d("ProfileViewModel", "Loading user profile for email: $email")

        if (!file.exists()) {
            Log.e("ProfileViewModel", "File $fileName does not exist.")
            return null
        }

        try {
            val reader = BufferedReader(FileReader(file))
            reader.useLines { lines ->
                lines.forEach { line ->
                    val data = line.split(",")

                    Log.d("ProfileViewModel", "Read line: $line")
                    Log.d("ProfileViewModel", "Data length: ${data.size}, Data: $data")

                    if (data.isNotEmpty() && data[0] == email) {
                        Log.d("ProfileViewModel", "Found matching profile for email: $email")

                        // Ensure the data has the expected number of elements
                        if (data.size >= 10) {
                            val userProfile = UserProfile(
                                fullName = data[3],
                                address = data[4],
                                city = data[5],
                                state = data[6],
                                zipcode = data[7],
                                skills = data[8].split("|"),
                                preferences = data[9],
                                availability = data.getOrNull(10) ?: ""
                            )

                            Log.d("ProfileViewModel", "Parsed UserProfile: $userProfile")
                            return userProfile
                        } else {
                            Log.e("ProfileViewModel", "Data for user $email is incomplete or corrupted. Expected at least 10 fields, but got ${data.size}.")
                        }
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("ProfileViewModel", "Error reading file: ${e.message}")
            e.printStackTrace()
        }

        Log.w("ProfileViewModel", "No matching profile found for email: $email")
        return null
    }

}
