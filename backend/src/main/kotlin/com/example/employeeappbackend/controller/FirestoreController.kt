package com.example.employeeappbackend.controller

import com.example.employeeappbackend.model.*
import com.example.employeeappbackend.service.FirestoreService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/firestore")
class FirestoreController(private val firestoreService: FirestoreService) {

    data class UserRegistrationRequest(
        val credentials: UserCredentials,
        val profile: UserProfile
    )

    @PostMapping("/register")
    fun registerUser(@RequestBody request: UserRegistrationRequest): ResponseEntity<String> {
        return if (firestoreService.registerUser(request.credentials, request.profile)) {
            ResponseEntity.ok("User registered successfully")
        } else {
            ResponseEntity.badRequest().body("Failed to register user")
        }
    }

    @PostMapping("/login")
    fun loginUser(@RequestBody credentials: UserCredentials): ResponseEntity<String> {
        return if (firestoreService.loginUser(credentials)) {
            ResponseEntity.ok("User logged in successfully")
        } else {
            ResponseEntity.status(401).body("Invalid credentials")
        }
    }

    @PostMapping("/profile/add")
    fun addUserProfile(@RequestBody profile: UserProfile): ResponseEntity<String> {
        return if (firestoreService.addUserProfile(profile)) {
            ResponseEntity.ok("Profile added successfully")
        } else {
            ResponseEntity.badRequest().body("Failed to add profile")
        }
    }

    @GetMapping("/profile/{fullName}")
    fun getUserProfile(@PathVariable fullName: String): ResponseEntity<UserProfile> {
        val profile = firestoreService.getUserProfile(fullName)
        return if (profile != null) {
            ResponseEntity.ok(profile)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/profile/update")
    fun updateUserProfile(@RequestBody profile: UserProfile): ResponseEntity<String> {
        return if (firestoreService.updateUserProfile(profile)) {
            ResponseEntity.ok("Profile updated successfully")
        } else {
            ResponseEntity.badRequest().body("Failed to update profile")
        }
    }

    @PostMapping("/event/create")
    fun createEvent(@RequestBody event: EventDetails): ResponseEntity<String> {
        return if (firestoreService.createEvent(event)) {
            ResponseEntity.ok("Event created successfully")
        } else {
            ResponseEntity.badRequest().body("Failed to create event")
        }
    }

    @PostMapping("/event/match")
    fun matchVolunteerToEvent(@RequestBody matchRequest: MatchRequest): ResponseEntity<String> {
        return if (firestoreService.matchVolunteerToEvent(matchRequest.volunteer, matchRequest.event)) {
            ResponseEntity.ok("Volunteer matched to event successfully")
        } else {
            ResponseEntity.badRequest().body("Failed to match volunteer to event")
        }
    }

    data class MatchRequest(val volunteer: UserProfile, val event: EventDetails)

    @PostMapping("/notify")
    fun notifyVolunteer(@RequestBody notificationRequest: NotificationRequest): ResponseEntity<String> {
        return if (firestoreService.notifyVolunteer(notificationRequest.volunteer, notificationRequest.message)) {
            ResponseEntity.ok("Notification sent successfully")
        } else {
            ResponseEntity.badRequest().body("Failed to send notification")
        }
    }

    data class NotificationRequest(val volunteer: UserProfile, val message: String)

    @PostMapping("/history/add")
    fun addVolunteerHistory(@RequestBody history: VolunteerHistory): ResponseEntity<String> {
        return if (firestoreService.trackVolunteerHistory(history)) {
            ResponseEntity.ok("Volunteer history added successfully")
        } else {
            ResponseEntity.badRequest().body("Failed to add volunteer history")
        }
    }
}
