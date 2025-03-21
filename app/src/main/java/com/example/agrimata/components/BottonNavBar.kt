package com.example.agrimata.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

sealed class BottomNavigationBar(val route: String, val title: String){
    sealed class BottomNavigationItem(val broute: String, val btitle: String, val icon: ImageVector): BottomNavigationBar(broute, btitle) {
        // user Bottom Navigation
        object Category : BottomNavigationItem("category", "Category", Icons.Default.Category)
        object UserProfile:BottomNavigationItem("profile","Profile",Icons.Default.Person)
        object Message : BottomNavigationItem("message", "Message", Icons.AutoMirrored.Filled.Chat)
        object Market:BottomNavigationItem("market","Market",Icons.Default.Storefront)

        // Farmer Bottom Navigation
        object Activity : BottomNavigationItem("activity", "Activity", Icons.AutoMirrored.Filled.ListAlt)
        object Crops : BottomNavigationItem("crops", "Crops", Icons.Default.Agriculture)
        object FarmerMessage : BottomNavigationItem("message", "Message", Icons.AutoMirrored.Filled.Chat)
        object FarmerProfile : BottomNavigationItem("farmerprofile", "Profile", Icons.Default.Person)
    }
}
val listOfUserBottomNavigationItems = listOf(
    BottomNavigationBar.BottomNavigationItem.Market,
    BottomNavigationBar.BottomNavigationItem.Category,
    BottomNavigationBar.BottomNavigationItem.Message,
    BottomNavigationBar.BottomNavigationItem.UserProfile
)

val listOfFarmerBottomNavigationItems = listOf(
    BottomNavigationBar.BottomNavigationItem.Activity,
    BottomNavigationBar.BottomNavigationItem.Crops,
    BottomNavigationBar.BottomNavigationItem.FarmerMessage,
    BottomNavigationBar.BottomNavigationItem.FarmerProfile
)

@Composable
fun UserBottomNavigationBarUi(navController: NavHostController) {

    val colorScheme = MaterialTheme.colorScheme
    val currentScreen = navController.currentBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .wrapContentSize()
            .offset(y = (0).dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(colorScheme.primary),
        contentAlignment = Alignment.Center
    )
 {
        NavigationBar(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            listOfUserBottomNavigationItems.forEach { screen ->
                NavigationBarItem(
                    selected = currentScreen == screen.broute,
                    onClick = {
                        navController.navigate(screen.broute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    label = {
                        Text(
                            text = screen.btitle,
                            color = if (currentScreen == screen.broute) colorScheme.onPrimary else colorScheme.onSecondary
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.btitle,
                            tint = if (currentScreen == screen.broute) colorScheme.onPrimary else colorScheme.onSecondary
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun FarmerBottomNavigationBarUi(navController: NavHostController) {

    val colorScheme = MaterialTheme.colorScheme
    val currentScreen = navController.currentBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .wrapContentSize()
            .offset(y = (0).dp)
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(colorScheme.primary),
        contentAlignment = Alignment.Center
    )
    {
        NavigationBar(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        ) {
            listOfFarmerBottomNavigationItems.forEach { screen ->
                NavigationBarItem(
                    selected = currentScreen == screen.broute,
                    onClick = {
                        navController.navigate(screen.broute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    label = {
                        Text(
                            text = screen.btitle,
                            color = if (currentScreen == screen.broute) colorScheme.onPrimary else colorScheme.onSecondary
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.btitle,
                            tint = if (currentScreen == screen.broute) colorScheme.onPrimary else colorScheme.onSecondary
                        )
                    }
                )
            }
        }
    }
}
