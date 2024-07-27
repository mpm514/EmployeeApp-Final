package com.example.employeeappbackend.repository

import com.example.employeeappbackend.model.*
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Repository
import java.util.concurrent.ExecutionException
import com.google.firebase.FirebaseApp
import org.slf4j.LoggerFactory

@Repository
class FirestoreRepository(private val firebaseApp: FirebaseApp) {

    val db: Firestore = FirestoreClient.getFirestore(firebaseApp)
    private val logger = LoggerFactory.getLogger(FirestoreRepository::class.java)

    fun addUserCredentials(credentials: UserCredentials): Boolean {
        val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        val securedCredentials = credentials.copy(password = hashedPassword)
        logger.debug("Registering user: ${credentials.id} with hashed password: $hashedPassword")
        return try {
            db.collection("UserCredentials")
                .document(credentials.id)
                .set(securedCredentials)
                .get()
            true
        } catch (e: ExecutionException) {
            logger.error("Error registering user", e)
            false
        } catch (e: InterruptedException) {
            logger.error("Error registering user", e)
            false
        }
    }

    fun checkUserCredentials(credentials: UserCredentials): Boolean {
        logger.debug("Attempting login for user: ${credentials.id}")
        return try {
            val document = db.collection("UserCredentials")
                .document(credentials.id)
                .get()
                .get()
            if (document.exists()) {
                val storedCredentials = document.toObject(UserCredentials::class.java)
                if (storedCredentials != null && BCrypt.checkpw(credentials.password, storedCredentials.password)) {
                    logger.debug("Login successful for user: ${credentials.id}")
                    true
                } else {
                    logger.debug("Login failed for user: ${credentials.id}")
                    false
                }
            } else {
                logger.debug("User not found: ${credentials.id}")
                false
            }
        } catch (e: ExecutionException) {
            logger.error("Error during login", e)
            false
        } catch (e: InterruptedException) {
            logger.error("Error during login", e)
            false
        }
    }

    fun addUserProfile(profile: UserProfile): Boolean {
        return try {
            db.collection("UserProfile")
                .document(profile.fullName)
                .set(profile)
                .get()
            true
        } catch (e: ExecutionException) {
            false
        } catch (e: InterruptedException) {
            false
        }
    }

    fun getUserProfile(fullName: String): UserProfile? {
        return try {
            val document = db.collection("UserProfile")
                .document(fullName)
                .get()
                .get()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun updateUserProfile(profile: UserProfile): Boolean {
        return try {
            db.collection("UserProfile")
                .document(profile.fullName)
                .set(profile)
                .get()
            true
        } catch (e: ExecutionException) {
            false
        } catch (e: InterruptedException) {
            false
        }
    }

    fun addEventDetails(event: EventDetails): Boolean {
        return try {
            db.collection("EventDetails")
                .document(event.eventName)
                .set(event)
                .get()
            true
        } catch (e: ExecutionException) {
            false
        } catch (e: InterruptedException) {
            false
        }
    }

    fun assignVolunteerToEvent(volunteer: UserProfile, event: EventDetails): Boolean {
        return try {
            val updatedEvent = event.copy(assignedVolunteers = event.assignedVolunteers + volunteer.fullName)
            db.collection("EventDetails")
                .document(event.eventName)
                .set(updatedEvent)
                .get()
            true
        } catch (e: ExecutionException) {
            false
        } catch (e: InterruptedException) {
            false
        }
    }

    fun sendNotification(volunteer: UserProfile, message: String): Boolean {
        // This is a placeholder for actual notification sending logic
        println("Sending notification to ${volunteer.email}: $message")
        return true
    }

    fun addVolunteerHistory(history: VolunteerHistory): Boolean {
        return try {
            db.collection("VolunteerHistory")
                .add(history)
                .get()
            true
        } catch (e: ExecutionException) {
            false
        } catch (e: InterruptedException) {
            false
        }
    }
}
