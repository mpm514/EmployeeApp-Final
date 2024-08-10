import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Profile : BottomNavItem("profile", Icons.Filled.Person, "Profile")
    object Events : BottomNavItem("eventManagement", Icons.Filled.List, "Events")
    object Matching : BottomNavItem("volunteerMatching", Icons.Filled.Search, "Matching")
    object History : BottomNavItem("history", Icons.Filled.Star, "History")
    object Reports : BottomNavItem("reports", Icons.Filled.Info, "Reports") // Added
}