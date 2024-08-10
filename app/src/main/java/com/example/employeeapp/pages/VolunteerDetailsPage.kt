package com.example.employeeapp.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.EventDetails as Event
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolunteerDetailsPage(navController: NavController, context: Context, volunteerName: String) {
    val events = remember { mutableStateListOf<Event>() }

    // Load events that the volunteer has participated in
    LaunchedEffect(volunteerName) {
        events.addAll(loadEventsForVolunteer(context, volunteerName))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Events for $volunteerName") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Events for $volunteerName", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(events) { event ->
                        EventListItem(event, isSelected = false) {
                            navController.navigate("viewEvent/${event.eventName}") // Navigate to the event page
                        }
                    }
                }
            }
        }
    )
}

fun loadEventsForVolunteer(context: Context, volunteerName: String): List<Event> {
    val fileName = "events.csv"
    val file = File(context.filesDir, fileName)
    val events = mutableListOf<Event>()
    if (!file.exists()) return events

    try {
        val reader = BufferedReader(FileReader(file))
        reader.useLines { lines ->
            lines.forEach { line ->
                val data = line.split(",")
                if (data.size >= 9 && data[8].split("|").contains(volunteerName)) {
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
                        assignedVolunteers = data[8].split("|")
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
