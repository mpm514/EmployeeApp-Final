package com.example.employeeapp.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.EventDetails
import com.example.employeeapp.navigation.BottomNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

@Composable
fun VolunteerMatchingPage(
    navController: NavController,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    isAdmin: Boolean // Pass isAdmin flag here
) {
    val events = remember { mutableStateListOf<EventDetails>() }

    // Load events from CSV file
    LaunchedEffect(Unit) {
        loadEvents(context, events)
    }

    var selectedEvent by remember { mutableStateOf<EventDetails?>(null) }
    val selectedVolunteers = remember { mutableStateListOf<String>() }

    // Sync selected volunteers when selectedEvent changes
    LaunchedEffect(selectedEvent) {
        selectedEvent?.let { event ->
            selectedVolunteers.clear()
            selectedVolunteers.addAll(event.assignedVolunteers)
            Log.d("VolunteerMatchingPage", "Loaded assigned volunteers: ${selectedVolunteers.joinToString()} for event: ${event.eventName}")
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Volunteer Matching", fontSize = 24.sp)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(0.5f)) {
                    items(events) { event ->
                        EventListItem(event, selectedEvent == event) {
                            selectedEvent = event
                        }
                    }
                }

                selectedEvent?.let { event ->
                    Text(text = "Matched Volunteers for ${event.eventName}:", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    val matchedVolunteers = findMatchingVolunteers(context, event)
                    LazyColumn(modifier = Modifier.weight(0.5f)) {
                        items(matchedVolunteers) { volunteer ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable(enabled = isAdmin) { // Allow only admins to modify the selection
                                        if (selectedVolunteers.contains(volunteer)) {
                                            selectedVolunteers.remove(volunteer)
                                            Log.d("VolunteerMatchingPage", "Removed volunteer: $volunteer from event: ${event.eventName}")
                                        } else {
                                            selectedVolunteers.add(volunteer)
                                            Log.d("VolunteerMatchingPage", "Added volunteer: $volunteer to event: ${event.eventName}")
                                        }
                                        saveAssignedVolunteers(context, event, selectedVolunteers)
                                    }
                            ) {
                                Checkbox(
                                    checked = selectedVolunteers.contains(volunteer),
                                    onCheckedChange = null // Disable checkbox interaction, handled by row click
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = volunteer, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun EventListItem(event: EventDetails, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(backgroundColor)
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = event.eventName, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = event.location, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Urgency: ${event.urgency}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Date: ${event.eventDate}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

fun loadEvents(context: Context, eventsList: MutableList<EventDetails>) {
    val fileName = "events.csv"
    val file = File(context.filesDir, fileName)
    if (!file.exists()) return

    val tempEvents = mutableListOf<EventDetails>()
    try {
        BufferedReader(FileReader(file)).use { reader ->
            reader.lineSequence().forEach { line ->
                val data = line.split(",")
                if (data.size >= 6) {
                    val event = EventDetails(
                        eventName = data[0],
                        description = data[1],
                        location = data[2],
                        requiredSkills = data[3].split("|"),
                        urgency = data[4],
                        eventDate = data[5],
                        creatorEmail = data[6],
                        isCreatorAdmin = data[7].toBoolean(),
                        assignedVolunteers = if (data.size > 8) data[8].split("|") else listOf()
                    )
                    tempEvents.add(event)
                    Log.d("VolunteerMatchingPage", "Loaded event: ${event.eventName} with assigned volunteers: ${event.assignedVolunteers.joinToString()}")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    eventsList.addAll(tempEvents)
}

fun findMatchingVolunteers(context: Context, event: EventDetails): List<String> {
    val volunteers = mutableListOf<String>()
    val fileName = "users.csv"
    val file = File(context.filesDir, fileName)
    if (!file.exists()) {
        Log.d("VolunteerMatchingPage", "Users file not found.")
        return listOf("No volunteers available based on event criteria.")
    }

    try {
        BufferedReader(FileReader(file)).use { reader ->
            reader.lineSequence().forEach { line ->
                val data = line.split(",")
                if (data.size >= 11) { // Ensure there are enough fields
                    val volunteerEmail = data[0]
                    val volunteerName = data[3]
                    val skills = data[8].split("|")
                    val availability = data[10].split("|")
                    val city = data[5]

                    val skillsMatch = event.requiredSkills.any { it in skills }
                    val availabilityMatch = event.eventDate in availability
                    val cityMatch = event.location.contains(city)

                    if (skillsMatch && availabilityMatch && cityMatch) {
                        volunteers.add(volunteerName)
                        Log.d("VolunteerMatchingPage", "Volunteer matched: $volunteerName for event: ${event.eventName}")
                    } else {
                        Log.d("VolunteerMatchingPage", "Volunteer did not match: $volunteerName")
                        Log.d("VolunteerMatchingPage", "Event Skills: ${event.requiredSkills.joinToString()}, Volunteer Skills: ${skills.joinToString()}")
                        Log.d("VolunteerMatchingPage", "Event Date: ${event.eventDate}, Volunteer Availability: ${availability.joinToString()}")
                        Log.d("VolunteerMatchingPage", "Event Location: ${event.location}, Volunteer City: $city")
                        Log.d("VolunteerMatchingPage", "Skills match: $skillsMatch, Availability match: $availabilityMatch, City match: $cityMatch")
                    }
                } else {
                    Log.d("VolunteerMatchingPage", "Skipped malformed user line: $line")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return volunteers
}

fun saveAssignedVolunteers(context: Context, event: EventDetails, assignedVolunteers: List<String>) {
    val fileName = "events.csv"
    val file = File(context.filesDir, fileName)
    val events = mutableListOf<EventDetails>()

    // Load existing events from the CSV
    if (file.exists()) {
        try {
            val reader = BufferedReader(FileReader(file))
            reader.useLines { lines ->
                lines.forEach { line ->
                    val data = line.split(",")
                    if (data.size >= 8) {
                        val skills = data[3].split("|")
                        val existingEvent = EventDetails(
                            eventName = data[0],
                            description = data[1],
                            location = data[2],
                            requiredSkills = skills,
                            urgency = data[4],
                            eventDate = data[5],
                            creatorEmail = data[6],
                            isCreatorAdmin = data[7].toBoolean(),
                            assignedVolunteers = if (data.size > 8) data[8].split("|") else listOf()
                        )
                        events.add(existingEvent)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Update the event with the newly assigned volunteers
    val eventIndex = events.indexOfFirst { it.eventName == event.eventName }
    if (eventIndex != -1) {
        events[eventIndex] = event.copy(assignedVolunteers = assignedVolunteers)
        Log.d("VolunteerMatchingPage", "Updated event: ${event.eventName} with new assigned volunteers: ${assignedVolunteers.joinToString()}")
    }

    // Save all events back to the CSV
    try {
        val writer = PrintWriter(FileWriter(file, false)) // Overwrite the entire file
        events.forEach { ev ->
            val skills = ev.requiredSkills.joinToString(separator = "|")
            val volunteers = ev.assignedVolunteers.joinToString(separator = "|")
            writer.println("${ev.eventName},${ev.description},${ev.location},$skills,${ev.urgency},${ev.eventDate},${ev.creatorEmail},${ev.isCreatorAdmin},$volunteers")
        }
        writer.close()
        Log.d("VolunteerMatchingPage", "Saved all events with updated volunteer assignments.")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
