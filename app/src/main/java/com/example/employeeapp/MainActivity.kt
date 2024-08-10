package com.example.employeeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.employeeapp.AuthViewModel
import com.example.employeeapp.MyAppNavigation
import com.example.employeeapp.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val snackbarHostState = SnackbarHostState()
            val scope = rememberCoroutineScope()
            val authViewModel: AuthViewModel = ViewModelProvider(
                this,
                AuthViewModel.AuthViewModelFactory(applicationContext)
            ).get(AuthViewModel::class.java)

            val userEmail by authViewModel.userEmail.observeAsState()

            if (userEmail != null) {
                MyAppNavigation(
                    authViewModel = authViewModel,
                    profileViewModel = viewModel(),
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    userEmail = userEmail!!,
                    context = applicationContext,
                    activity = this
                )
            }
        }
    }
}