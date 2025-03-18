package com.example.agrimata.components

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
fun BottomNavigationBarUi(navController: NavHostController){

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground

    val currentScreen = navController.currentBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = backgroundColor,
        contentColor = textColor,
        tonalElevation = 5.dp,

    ){
        listOfBottomNavigationItems.forEach{ screens ->
            NavigationBarItem(
                selected = currentScreen == screens.broute,
                onClick = {
                    navController.navigate(screens.broute){
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it){
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = { Text(text = screens.btitle, color = if (currentScreen == screens.broute) primaryColor else secondaryColor) },
                icon = {
                    Icon(imageVector = screens.icon,
                        contentDescription = screens.btitle,
                        tint = if (currentScreen == screens.broute) primaryColor else secondaryColor)}
            )
        }
    }
}