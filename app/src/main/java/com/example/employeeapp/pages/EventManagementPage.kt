package com.example.employeeapp.pages

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import java.io.IOException

@Composable
fun EventManagementPage(
    navController: NavController,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    // Remember the list of events
    var events by remember { mutableStateOf(loadEventsFromCsvManagement(context)) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomNavigationBar(navController = navController) }, // Add BottomNavigationBar here
        content = { paddingValues ->  // Pass padding values here
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Apply padding here
                    .padding(16.dp)
            ) {
                Text(text = "Event Management", fontSize = 24.sp)

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(events) { event ->
                        EventListItem(event) {
                            navController.navigate("editEvent/${event.eventName}")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        navController.navigate("createEvent")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Event")
                }
            }
        }
    )

    // Whenever we return to the screen, re-load the events to ensure they're up-to-date
    LaunchedEffect(Unit) {
        events = loadEventsFromCsvManagement(context)
    }
}

@Composable
fun EventListItem(event: EventDetails, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
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

// Function to load events from a CSV file specific to EventManagementPage
fun loadEventsFromCsvManagement(context: Context): List<EventDetails> {
    val fileName = "events.csv"
    val file = File(context.filesDir, fileName)
    val events = mutableListOf<EventDetails>()
    if (!file.exists()) return events

    try {
        val reader = BufferedReader(FileReader(file))
        reader.useLines { lines ->
            lines.forEach { line ->
                val data = line.split(",")
                if (data.size >= 8) {
                    val skills = data[3].split("|")
                    val event = EventDetails(
                        eventName = data[0],
                        description = data[1],
                        location = data[2],
                        requiredSkills = skills,
                        urgency = data[4],
                        eventDate = data[5],
                        creatorEmail = data[6],
                        isCreatorAdmin = data[7].toBoolean()
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
