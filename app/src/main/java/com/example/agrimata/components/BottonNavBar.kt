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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBasket
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
        object Home : BottomNavigationItem("home", "Home", Icons.Default.Home)
        object Search : BottomNavigationItem("category", "Category", Icons.Default.Category)
        object Profile:BottomNavigationItem("profile","Profile",Icons.Default.Person)
        object Market:BottomNavigationItem("market","Market",Icons.Default.ShoppingBasket)

    }
}
val listOfBottomNavigationItems = listOf(
    BottomNavigationBar.BottomNavigationItem.Market,
    BottomNavigationBar.BottomNavigationItem.Search,
    BottomNavigationBar.BottomNavigationItem.Profile
)

@Composable
fun BottomNavigationBarUi(navController: NavHostController) {

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
            listOfBottomNavigationItems.forEach { screen ->
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
