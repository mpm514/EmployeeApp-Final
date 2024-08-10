package com.example.employeeapp.pages

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.employeeapp.AuthState
import com.example.employeeapp.AuthViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun SignupPage(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.observeAsState()
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate("profile?isFromSignup=true") {
                    popUpTo("signup") { inclusive = true }
                }
                authViewModel.setMessage("Please complete your profile.")
            }
            is AuthState.Error -> {
                showError = true
                errorMessage = (authState as AuthState.Error).message
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Account Registration", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isAdmin,
                onCheckedChange = { isAdmin = it }
            )
            Text(text = "Register as Admin", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.signup(email, password, isAdmin)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Signup")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(text = "Already have an account? Login Here.")
        }

        if (showError) {
            Snackbar(
                action = {
                    TextButton(onClick = { showError = false }) {
                        Text(text = "Dismiss")
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = errorMessage)
            }
        }
    }
}
