package com.example.employeeappbackend.repository

import com.example.employeeappbackend.model.*
import com.google.cloud.firestore.DocumentReference
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.google.firebase.FirebaseApp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class FirestoreRepositoryTest {

    private lateinit var firebaseApp: FirebaseApp
    private lateinit var firestore: Firestore
    private lateinit var firestoreRepository: FirestoreRepository

    @BeforeEach
    fun setUp() {
        firebaseApp = mock(FirebaseApp::class.java)
        firestore = mock(Firestore::class.java)
        firestoreRepository = FirestoreRepository(firebaseApp)
        firestoreRepository.db = firestore
    }

    @Test
    fun `addUserCredentials should hash password and store credentials`() {
        val documentReference = mock(DocumentReference::class.java)
        `when`(firestore.collection("UserCredentials").document("test@example.com")).thenReturn(documentReference)

        val credentials = UserCredentials("test@example.com", "password123")
        val result = firestoreRepository.addUserCredentials(credentials)
        assertTrue(result)
        assertNotEquals("password123", credentials.password)
        verify(documentReference).set(credentials, SetOptions.merge())
    }

    @Test
    fun `checkUserCredentials should verify password`() {
        val credentials = UserCredentials("test@example.com", "password123")
        `when`(firestore.collection("UserCredentials").document("test@example.com").get().get().toObject(UserCredentials::class.java)).thenReturn(credentials)

        val result = firestoreRepository.checkUserCredentials(credentials)
        assertTrue(result)
    }

    @Test
    fun `addUserProfile should store profile`() {
        val documentReference = mock(DocumentReference::class.java)
        `when`(firestore.collection("UserProfile").document("Test User")).thenReturn(documentReference)

        val profile = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")
        val result = firestoreRepository.addUserProfile(profile)
        assertTrue(result)
        verify(documentReference).set(profile, SetOptions.merge())
    }

    @Test
    fun `getUserProfile should retrieve profile`() {
        val profile = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")
        `when`(firestore.collection("UserProfile").document("Test User").get().get().toObject(UserProfile::class.java)).thenReturn(profile)

        val result = firestoreRepository.getUserProfile("Test User")
        assertNotNull(result)
        assertEquals(profile, result)
    }

    @Test
    fun `updateUserProfile should update profile`() {
        val documentReference = mock(DocumentReference::class.java)
        `when`(firestore.collection("UserProfile").document("Test User")).thenReturn(documentReference)

        val profile = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")
        val result = firestoreRepository.updateUserProfile(profile)
        assertTrue(result)
        verify(documentReference).set(profile, SetOptions.merge())
    }

    @Test
    fun `addEventDetails should store event`() {
        val documentReference = mock(DocumentReference::class.java)
        `when`(firestore.collection("EventDetails").document("Test Event")).thenReturn(documentReference)

        val event = EventDetails("Test Event", "This is a test event", "Test City", listOf("Skill1"), "High", "2024-07-31", emptyList())
        val result = firestoreRepository.addEventDetails(event)
        assertTrue(result)
        verify(documentReference).set(event, SetOptions.merge())
    }

    @Test
    fun `assignVolunteerToEvent should assign volunteer`() {
        val documentReference = mock(DocumentReference::class.java)
        `when`(firestore.collection("EventDetails").document("Test Event")).thenReturn(documentReference)

        val volunteer = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")
        val event = EventDetails("Test Event", "This is a test event", "Test City", listOf("Skill1"), "High", "2024-07-31", emptyList())
        val result = firestoreRepository.assignVolunteerToEvent(volunteer, event)
        assertTrue(result)
        verify(documentReference).set(event.copy(assignedVolunteers = event.assignedVolunteers + volunteer.fullName), SetOptions.merge())
    }

    @Test
    fun `sendNotification should send notification`() {
        val volunteer = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")
        val result = firestoreRepository.sendNotification(volunteer, "This is a test notification")
        assertTrue(result)
    }

    @Test
    fun `addVolunteerHistory should store history`() {
        val documentReference = mock(DocumentReference::class.java)
        `when`(firestore.collection("VolunteerHistory").add(any(VolunteerHistory::class.java))).thenReturn(documentReference)

        val history = VolunteerHistory("Test User", "Test Event", "2024-07-31", 5)
        val result = firestoreRepository.addVolunteerHistory(history)
        assertTrue(result)
        verify(firestore.collection("VolunteerHistory")).add(history)
    }
}
