package com.example.employeeappbackend.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseApp(): FirebaseApp {
        try {
            val serviceAccount = FileInputStream("/home/michael/Documents/EmployeeApp-Latest/backend/employeeapp-47004-firebase-adminsdk-95r3f-cfaf4ced70.json")
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()
            return FirebaseApp.initializeApp(options)
        } catch (e: Exception) {
            // Handle exceptions (e.g., log the error or throw a custom exception)
            println("Error initializing Firebase: ${e.message}")
            throw e
        }
    }
}
