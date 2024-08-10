package com.example.employeeapp.pages

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.AuthViewModel
import com.example.employeeapp.EventDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.util.*
import androidx.compose.runtime.livedata.observeAsState
import java.io.BufferedReader
import java.io.FileReader


@Composable
fun CreateOrEditEventPage(
    navController: NavController,
    context: Context,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    event: EventDetails? = null,
    authViewModel: AuthViewModel,
    activity: ComponentActivity
) {
    val currentUserEmail by authViewModel.userEmail.observeAsState("")
    var currentUserIsAdmin by remember { mutableStateOf(false) }

    // LaunchedEffect to check admin status when the composable is launched
    LaunchedEffect(Unit) {
        currentUserIsAdmin = authViewModel.validateUserCredentials(context, currentUserEmail) == true
    }

    // Determine if the user can edit the event
    val canEditEvent = currentUserIsAdmin || (event != null && event.creatorEmail == currentUserEmail)

    // Log the results for debugging
    var eventName by remember { mutableStateOf(event?.eventName ?: "") }
    var eventDescription by remember { mutableStateOf(event?.description ?: "") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var selectedSkills by remember { mutableStateOf(event?.requiredSkills ?: listOf()) }
    var urgency by remember { mutableStateOf(event?.urgency ?: "") }
    var eventDate by remember { mutableStateOf("") }
    var availabilityDates by remember { mutableStateOf(event?.eventDate?.split("|")?.toMutableList() ?: mutableListOf()) }

    // Function to validate input fields
    fun validateFields(): Boolean {
        return eventName.isNotBlank() &&
                eventDescription.isNotBlank() &&
                location.isNotBlank() &&
                selectedSkills.isNotEmpty() &&
                urgency.isNotBlank() &&
                availabilityDates.isNotEmpty()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = eventName,
                    onValueChange = { if (canEditEvent) eventName = it },
                    label = { Text("Event Name") },
                    enabled = canEditEvent,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = eventDescription,
                    onValueChange = { if (canEditEvent) eventDescription = it },
                    label = { Text("Event Description") },
                    enabled = canEditEvent,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { if (canEditEvent) location = it },
                    label = { Text("Location") },
                    enabled = canEditEvent,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Skills Dropdown
                var skillsExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = selectedSkills.joinToString(", "),
                        onValueChange = { /* Handle Skills selection */ },
                        label = { Text("Skills Required") },
                        enabled = canEditEvent,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { skillsExpanded = !skillsExpanded }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = skillsExpanded,
                        onDismissRequest = { skillsExpanded = false }
                    ) {
                        val skillsList = listOf("Programming", "Teaching", "Guitar", "Mathematics", "Carpentry")
                        skillsList.forEach { skill ->
                            DropdownMenuItem(
                                text = { Text(skill) },
                                onClick = {
                                    if (!selectedSkills.contains(skill)) {
                                        selectedSkills = selectedSkills + skill
                                    } else {
                                        selectedSkills = selectedSkills - skill
                                    }
                                    skillsExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Urgency Dropdown
                var urgencyExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = urgency,
                        onValueChange = { /* Handle Urgency selection */ },
                        label = { Text("Urgency Level") },
                        enabled = canEditEvent,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { urgencyExpanded = !urgencyExpanded }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = urgencyExpanded,
                        onDismissRequest = { urgencyExpanded = false }
                    ) {
                        val urgencyLevels = listOf("Low", "Medium", "High")
                        urgencyLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level) },
                                onClick = {
                                    urgency = level
                                    urgencyExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Availability Date Picker
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                var selectedDate by remember { mutableStateOf("") }

                val datePickerDialog = DatePickerDialog(
                    activity,
                    //context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        selectedDate = "${selectedMonth + 1}/$selectedDay/$selectedYear"
                        availabilityDates.add(selectedDate)
                        Log.d("DatePicker", "Selected Date: $selectedDate")  // Add log here
                    }, year, month, day
                )

                OutlinedTextField(
                    value = eventDate,
                    onValueChange = { eventDate = it },
                    label = { Text("Add Availability Date") },
                    enabled = canEditEvent,
                    trailingIcon = {
                        IconButton(onClick = {
                            datePickerDialog.show()
                        }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Date")
                        }
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    availabilityDates.forEach { date ->
                        Text(text = date, modifier = Modifier.padding(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (canEditEvent && validateFields()) {
                            val newEvent = EventDetails(
                                eventName = eventName,
                                description = eventDescription,
                                location = location,
                                requiredSkills = selectedSkills,
                                urgency = urgency,
                                eventDate = availabilityDates.joinToString(separator = "|"),
                                creatorEmail = currentUserEmail ?: "",
                                isCreatorAdmin = currentUserIsAdmin
                            )
                            saveEventToCsvInEditPage(context, newEvent)
                            navController.navigate("eventManagement")
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Event $eventName on $eventDate was saved.",
                                    actionLabel = "View",
                                    duration = SnackbarDuration.Indefinite
                                )
                            }
                        }
                    },
                    enabled = canEditEvent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Event")
                }

                // Delete Event Button
                if (event != null && currentUserIsAdmin) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            deleteEventFromCsv(context, event)
                            navController.navigate("eventManagement")
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Event $eventName was deleted.",
                                    actionLabel = "OK",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Event")
                    }
                }
            }
        }
    )
}

// Function to save the event to a CSV file
fun saveEventToCsvInEditPage(context: Context, event: EventDetails) {
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
                            isCreatorAdmin = data[7].toBoolean()
                        )
                        events.add(existingEvent)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Check if the event already exists
    val existingEventIndex = events.indexOfFirst { it.eventName == event.eventName }

    if (existingEventIndex != -1) {
        events[existingEventIndex] = event
    } else {
        events.add(event)
    }

    // Write all events back to the CSV file
    try {
        val writer = PrintWriter(FileWriter(file, false)) // Overwrite the entire file
        events.forEach { ev ->
            val skills = ev.requiredSkills.joinToString(separator = "|")
            writer.println("${ev.eventName},${ev.description},${ev.location},$skills,${ev.urgency},${ev.eventDate},${ev.creatorEmail},${ev.isCreatorAdmin}")
        }
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

// Function to delete an event from the CSV file
fun deleteEventFromCsv(context: Context, event: EventDetails) {
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
                            isCreatorAdmin = data[7].toBoolean()
                        )
                        events.add(existingEvent)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Remove the event from the list
    events.removeIf { it.eventName == event.eventName }

    // Write all events back to the CSV file
    try {
        val writer = PrintWriter(FileWriter(file, false)) // Overwrite the entire file
        events.forEach { ev ->
            val skills = ev.requiredSkills.joinToString(separator = "|")
            writer.println("${ev.eventName},${ev.description},${ev.location},$skills,${ev.urgency},${ev.eventDate},${ev.creatorEmail},${ev.isCreatorAdmin}")
        }
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
