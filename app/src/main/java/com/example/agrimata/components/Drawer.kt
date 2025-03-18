package com.example.agrimata.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

sealed class DrawerNavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object EditProfile : DrawerNavigationItem("editprofile", "Edit Profile", Icons.Default.Edit)
    object Settings : DrawerNavigationItem("setting", "Settings", Icons.Default.Settings)
    object AboutUs : DrawerNavigationItem("aboutus", "About Us", Icons.Default.Info)
    object ContactUs : DrawerNavigationItem("contactus", "Contact Us", Icons.Default.Phone)
}

val listOfDrawerItems = listOf(
    DrawerNavigationItem.EditProfile,
    DrawerNavigationItem.Settings,
    DrawerNavigationItem.AboutUs,
    DrawerNavigationItem.ContactUs
)

@Composable
fun DrawerMenu(
    navController: NavController,
    drawerState: DrawerState,
    closeDrawer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight().background(backgroundColor),
        drawerContainerColor = backgroundColor
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        listOfDrawerItems.forEach { item ->
            DrawerItem(icon = item.icon, label = item.title) {
                navController.navigate(item.route) {
                    navController.graph.startDestinationRoute?.let {
                        popUpTo(it) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
                scope.launch { drawerState.close() }
            }
        }
    }
}

@Composable
fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerMenuPreview() {
    DrawerMenu(navController = rememberNavController(), drawerState = rememberDrawerState(DrawerValue.Closed)) {}
}