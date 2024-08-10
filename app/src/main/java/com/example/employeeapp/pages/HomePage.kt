package com.example.employeeapp.pages

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.AuthState
import com.example.employeeapp.AuthViewModel
import com.example.employeeapp.EventDetails
import com.example.employeeapp.navigation.BottomNavigationBar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    context: Context
) {
    val authState by authViewModel.authState.observeAsState()
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    val events = remember { mutableStateListOf<EventDetails>().apply { addAll(loadEventsFromCsvManagement(context)) } }

    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedDate = changeDate(selectedDate, -1) }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Previous Day")
                        }
                        Text(
                            text = selectedDate,
                            fontSize = 20.sp, // Adjusted font size
                        )
                        IconButton(onClick = { selectedDate = changeDate(selectedDate, 1) }) {
                            Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Next Day")
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                val filteredEvents = events.filter {
                    Log.d("EventFiltering", "Comparing eventDate: ${normalizeDate(it.eventDate)} with selectedDate: $selectedDate")
                    normalizeDate(it.eventDate) == selectedDate
                }

                // Log the filtered events and the selected date
                Log.d("HomePage", "Selected Date: $selectedDate")
                Log.d("HomePage", "Filtered Events: ${filteredEvents.map { it.eventName }}")

                if (filteredEvents.isEmpty()) {
                    Text(text = "No events scheduled.", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                } else {
                    EventsList(filteredEvents)
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = {
                    authViewModel.signout()
                }) {
                    Text(text = "Sign out")
                }
            }
        }
    )
}

fun normalizeDate(date: String): String {
    val sdfInput = SimpleDateFormat("M/d/yyyy", Locale.US) // Handles single-digit months and days
    val sdfOutput = SimpleDateFormat("MM/dd/yyyy", Locale.US) // Ensures output is always two digits
    return sdfOutput.format(sdfInput.parse(date) ?: Date())
}


@Composable
fun EventsList(events: List<EventDetails>) {
    LazyColumn {
        items(events) { event ->
            EventItem(event)
        }
    }
}

@Composable
fun EventItem(event: EventDetails) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(text = event.eventName, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Location: ${event.location}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Urgency: ${event.urgency}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Date: ${event.eventDate}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US) // Changed to US date format
    return sdf.format(Date())
}

fun changeDate(currentDate: String, daysToAddOrSubtract: Int): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US) // Changed to US date format
    val calendar = Calendar.getInstance()
    calendar.time = sdf.parse(currentDate) ?: Date()
    calendar.add(Calendar.DAY_OF_YEAR, daysToAddOrSubtract)
    return sdf.format(calendar.time)
}
