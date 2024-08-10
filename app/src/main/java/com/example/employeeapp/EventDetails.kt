package com.example.employeeapp

data class EventDetails(
    val eventName: String = "",
    val description: String = "",
    val location: String = "",
    val requiredSkills: List<String> = listOf(),
    val urgency: String = "",
    val eventDate: String = "",
    var assignedVolunteers: List<String> = listOf(),
    val creatorEmail: String = "",          // Add this line
    val isCreatorAdmin: Boolean = false     // Add this line
)
