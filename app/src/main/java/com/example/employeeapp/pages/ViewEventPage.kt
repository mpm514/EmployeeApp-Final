package com.example.employeeapp.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.EventDetails

@Composable
fun ViewEventPage(navController: NavController, event: EventDetails?) {
    event?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Event Details", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Event Name: ${event.eventName}", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Description: ${event.description}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Location: ${event.location}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Urgency: ${event.urgency}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date: ${event.eventDate}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Assigned Volunteers: ${event.assignedVolunteers.joinToString()}", fontSize = 16.sp)
        }
    } ?: run {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Event not found", color = Color.Red, fontSize = 20.sp)
        }
    }
}
