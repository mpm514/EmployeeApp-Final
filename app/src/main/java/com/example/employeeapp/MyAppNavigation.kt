package com.example.employeeapp

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.employeeapp.pages.*
import com.example.employeeapp.EventDetails
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    userEmail: String,
    context: Context,
    activity: ComponentActivity
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginPage(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("signup") {
            SignupPage(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("profile?isFromSignup={isFromSignup}") { backStackEntry ->
            val isFromSignup = backStackEntry.arguments?.getString("isFromSignup")?.toBoolean() ?: false
            ProfilePage(
                navController = navController,
                profileViewModel = profileViewModel,
                isFromSignup = isFromSignup,
                userEmail = userEmail
            )
        }
        composable("home") {
            HomePage(
                navController = navController,
                authViewModel = authViewModel,
                context = context
            )
        }
        composable("mainScreen") {
            MainScreen(
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                snackbarHostState = snackbarHostState,
                scope = scope,
                userEmail = userEmail
            )
        }
        composable("profile") {
            ProfilePage(
                navController = navController,
                profileViewModel = profileViewModel,
                userEmail = userEmail
            )
        }
        composable("eventManagement") {
            EventManagementPage(
                navController = navController,
                context = context,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
        composable("volunteerMatching") {
            val isAdmin = authViewModel.validateUserCredentials(context, userEmail) ?: false

            VolunteerMatchingPage(
                navController = navController,
                context = context,
                snackbarHostState = snackbarHostState,
                scope = scope,
                isAdmin = isAdmin
            )
        }
        composable("history") {
            VolunteerHistoryPage(
                navController = navController,
                context = context
            )
        }
        composable("notifications") {
            NotificationsPage(
                navController = navController
            )
        }
        composable("createEvent") {
            CreateOrEditEventPage(
                navController = navController,
                context = context,
                snackbarHostState = snackbarHostState,
                scope = scope,
                authViewModel = authViewModel,
                activity = activity
            )
        }
        composable("editEvent/{eventName}") { backStackEntry ->
            val eventName = backStackEntry.arguments?.getString("eventName")
            val event = findEventByName(context, eventName)
            CreateOrEditEventPage(
                navController = navController,
                context = context,
                snackbarHostState = snackbarHostState,
                scope = scope,
                event = event,
                authViewModel = authViewModel,
                activity = activity
            )
        }
        composable("volunteerProfile/{volunteerName}") { backStackEntry ->
            val volunteerName = backStackEntry.arguments?.getString("volunteerName")
            VolunteerProfilePage(
                navController = navController,
                volunteerName = volunteerName ?: "",
                context = context
            )
        }

        composable("volunteerDetails/{volunteerName}") { backStackEntry ->
            val volunteerName = backStackEntry.arguments?.getString("volunteerName") ?: ""
            VolunteerDetailsPage(
                navController = navController,
                context = context,
                volunteerName = volunteerName
            )
        }

        composable("reports") {
            ReportsPage(context = context)
        }


    }
}

// Single definition for findEventByName
fun findEventByName(context: Context, eventName: String?): EventDetails? {
    val events = loadEventsFromCsv(context)
    return events.find { it.eventName == eventName }
}
