package com.example.employeeapp.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.EventDetails as Event
import com.example.employeeapp.navigation.BottomNavigationBar
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

@Composable
fun VolunteerHistoryPage(navController: NavController, context: Context) {
    val events = remember { mutableStateListOf<Event>() }
    val volunteersForEvents = remember { mutableStateOf<Map<String, List<Pair<String, String>>>>(emptyMap()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    // Load events from CSV
    LaunchedEffect(Unit) {
        events.addAll(loadEventsFromCsv(context))
        volunteersForEvents.value = loadVolunteersForEvents(context)
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
                Text(text = "Volunteer History", fontSize = 24.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Events for ${com.example.employeeapp.common.getCurrentDate()}:")
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(0.5f)) {
                    items(events) { event ->
                        EventListItem(event, isSelected = (selectedEvent == event)) {
                            selectedEvent = event
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Assigned Volunteers:")
                Spacer(modifier = Modifier.height(8.dp))

                val volunteers = selectedEvent?.let { volunteersForEvents.value[it.eventName] } ?: emptyList()

                if (volunteers.isEmpty()) {
                    Text(text = "No volunteers available based on event criteria", color = Color.Red)
                } else {
                    LazyColumn(modifier = Modifier.weight(0.5f)) {
                        items(volunteers) { volunteer ->
                            VolunteerHistoryListItem(volunteer, navController) // Pass the navController here
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun VolunteerHistoryListItem(volunteer: Pair<String, String>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .clickable {
                // Navigate to the VolunteerDetailsPage passing the volunteer's full name
                navController.navigate("volunteerDetails/${volunteer.first}")
            }
    ) {
        Text(text = volunteer.first, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}


// Helper function to load events from the CSV file
fun loadEventsFromCsv(context: Context): List<Event> {
    val fileName = "events.csv"
    val file = File(context.filesDir, fileName)
    val events = mutableListOf<Event>()
    if (!file.exists()) return events

    try {
        val reader = BufferedReader(FileReader(file))
        reader.useLines { lines ->
            lines.forEach { line ->
                val data = line.split(",")
                if (data.size >= 8) {
                    val skills = data[3].split("|")
                    val event = Event(
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
                    events.add(event)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return events
}

// Helper function to load volunteers for events from the CSV file
fun loadVolunteersForEvents(context: Context): Map<String, List<Pair<String, String>>> {
    val fileName = "events.csv"
    val file = File(context.filesDir, fileName)
    val volunteersForEvents = mutableMapOf<String, List<Pair<String, String>>>()

    if (!file.exists()) return volunteersForEvents

    try {
        val reader = BufferedReader(FileReader(file))
        reader.useLines { lines ->
            lines.forEach { line ->
                val data = line.split(",")
                if (data.size >= 9) {
                    val eventName = data[0].trim()
                    val volunteers = data[8].split("|").map { it.trim() }

                    Log.d("VolunteerHistoryPage", "Processing event: $eventName with volunteers: ${volunteers.joinToString()}")

                    val volunteerPairs = volunteers.mapNotNull { fullName ->
                        val email = loadVolunteerEmailByName(context, fullName)
                        if (email != null) fullName to email else null
                    }

                    volunteersForEvents[eventName] = volunteerPairs
                    Log.d("VolunteerHistoryPage", "Added volunteers for event: $eventName -> ${volunteerPairs.joinToString { it.first }}")
                } else {
                    Log.d("VolunteerHistoryPage", "Skipping malformed event line: $line")
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return volunteersForEvents
}

// Function to load the volunteer's email by full name from the users.csv file
fun loadVolunteerEmailByName(context: Context, fullName: String): String? {
    val fileName = "users.csv"
    val file = File(context.filesDir, fileName)
    if (!file.exists()) return null

    try {
        val reader = BufferedReader(FileReader(file))
        reader.useLines { lines ->
            lines.forEach { line ->
                val data = line.split(",")
                if (data.size >= 3 && data[3] == fullName) { // Assuming the full name is in the third position (index 2)
                    return data[0] // Assuming the email is in the first position (index 0)
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}
