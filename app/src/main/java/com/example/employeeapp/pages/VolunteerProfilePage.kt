package com.example.employeeapp.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.UserProfile
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

@Composable
fun VolunteerProfilePage(navController: NavController, volunteerName: String, context: Context) {
    val volunteer = remember { loadVolunteerProfile(context, volunteerName) }

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Volunteer Profile", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))

                volunteer?.let {
                    Text(text = "Full Name: ${it.fullName}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Address: ${it.address}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "City: ${it.city}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "State: ${it.state}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Zip Code: ${it.zipcode}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Skills: ${it.skills.joinToString()}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Preferences: ${it.preferences}", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Availability: ${it.availability}", fontSize = 20.sp)
                } ?: run {
                    Text(text = "Volunteer not found.", fontSize = 20.sp, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { navController.popBackStack() }) {
                    Text("Back")
                }
            }
        }
    )
}

fun loadVolunteerProfile(context: Context, volunteerName: String): UserProfile? {
    val fileName = "users.csv"
    val file = File(context.filesDir, fileName)
    if (!file.exists()) return null

    try {
        val reader = BufferedReader(FileReader(file))
        reader.useLines { lines ->
            lines.forEach { line ->
                val data = line.split(",")
                if (data.isNotEmpty() && data[0] == volunteerName) {
                    val skills = data[5].split("|")
                    return UserProfile(
                        fullName = data[0],
                        address = data[1],
                        city = data[2],
                        state = data[3],
                        zipcode = data[4],
                        skills = skills,
                        preferences = data[6],
                        availability = data[7]
                    )
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}
