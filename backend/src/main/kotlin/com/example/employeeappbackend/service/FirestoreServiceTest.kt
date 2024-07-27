package com.example.employeeappbackend.service

import com.example.employeeappbackend.model.UserCredentials
import com.example.employeeappbackend.model.UserProfile
import com.example.employeeappbackend.model.EventDetails
import com.example.employeeappbackend.model.VolunteerHistory
import com.example.employeeappbackend.repository.FirestoreRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class FirestoreServiceTest {

    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var firestoreService: FirestoreService

    @BeforeEach
    fun setUp() {
        firestoreRepository = mock(FirestoreRepository::class.java)
        firestoreService = FirestoreService(firestoreRepository)
    }

    @Test
    fun `registerUser should validate and store user`() {
        val credentials = UserCredentials("test@example.com", "password123")
        val profile = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")

        `when`(firestoreRepository.addUserCredentials(any(UserCredentials::class.java))).thenReturn(true)
        `when`(firestoreRepository.addUserProfile(any(UserProfile::class.java))).thenReturn(true)

        val result = firestoreService.registerUser(credentials, profile)
        assertTrue(result)
    }

    @Test
    fun `loginUser should validate and authenticate user`() {
        val credentials = UserCredentials("test@example.com", "password123")

        `when`(firestoreRepository.checkUserCredentials(any(UserCredentials::class.java))).thenReturn(true)

        val result = firestoreService.loginUser(credentials)
        assertTrue(result)
    }

    @Test
    fun `updateUserProfile should update profile`() {
        val profile = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")

        `when`(firestoreRepository.updateUserProfile(any(UserProfile::class.java))).thenReturn(true)

        val result = firestoreService.updateUserProfile(profile)
        assertTrue(result)
    }

    @Test
    fun `createEvent should store event`() {
        val event = EventDetails("Test Event", "This is a test event", "Test City", listOf("Skill1"), "High", "2024-07-31", emptyList())

        `when`(firestoreRepository.addEventDetails(any(EventDetails::class.java))).thenReturn(true)

        val result = firestoreService.createEvent(event)
        assertTrue(result)
    }

    @Test
    fun `matchVolunteerToEvent should assign volunteer`() {
        val volunteer = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")
        val event = EventDetails("Test Event", "This is a test event", "Test City", listOf("Skill1"), "High", "2024-07-31", emptyList())

        `when`(firestoreRepository.assignVolunteerToEvent(any(UserProfile::class.java), any(EventDetails::class.java))).thenReturn(true)

        val result = firestoreService.matchVolunteerToEvent(volunteer, event)
        assertTrue(result)
    }

    @Test
    fun `notifyVolunteer should send notification`() {
        val volunteer = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")

        `when`(firestoreRepository.sendNotification(any(UserProfile::class.java), any(String::class.java))).thenReturn(true)

        val result = firestoreService.notifyVolunteer(volunteer, "This is a test notification")
        assertTrue(result)
    }

    @Test
    fun `trackVolunteerHistory should store history`() {
        val history = VolunteerHistory("Test User", "Test Event", "2024-07-31", 5)

        `when`(firestoreRepository.addVolunteerHistory(any(VolunteerHistory::class.java))).thenReturn(true)

        val result = firestoreService.trackVolunteerHistory(history)
        assertTrue(result)
    }

    @Test
    fun `addUserProfile should store profile`() {
        val profile = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")

        `when`(firestoreRepository.addUserProfile(any(UserProfile::class.java))).thenReturn(true)

        val result = firestoreService.addUserProfile(profile)
        assertTrue(result)
    }

    @Test
    fun `getUserProfile should retrieve profile`() {
        val profile = UserProfile("Test User", "test@example.com", "123 Main St", "Test City", "TS", "12345", listOf("Skill1"), listOf("Preference1"), "Weekdays")

        `when`(firestoreRepository.getUserProfile(any(String::class.java))).thenReturn(profile)

        val result = firestoreService.getUserProfile("Test User")
        assertNotNull(result)
        assertEquals(profile, result)
    }
}