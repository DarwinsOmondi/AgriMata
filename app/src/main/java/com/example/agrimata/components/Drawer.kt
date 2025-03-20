package com.example.agrimata.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.agrimata.network.SuparBaseClient
import com.example.agrimata.network.SuparBaseClient.client
import io.github.jan.supabase.gotrue.gotrue
import kotlinx.coroutines.launch

sealed class DrawerNavigationItem(val route: String, val title: String, val icon: ImageVector) {

    //User Darwer Navigation Items
    object EditProfile : DrawerNavigationItem("editprofile", "Edit Profile", Icons.Default.Edit)
    object FarmerAccount : DrawerNavigationItem("farmeraccount", "Farmer Account", Icons.Default.Agriculture)
    object Settings : DrawerNavigationItem("setting", "Settings", Icons.Default.Settings)
    object AboutUs : DrawerNavigationItem("aboutus", "About Us", Icons.Default.Info)
    object ContactUs : DrawerNavigationItem("contactus", "Contact Us", Icons.Default.Phone)

    // Farmer Darwer Navigation Items
    object Orders : DrawerNavigationItem("orders", "Orders", Icons.Default.ShoppingCart)
    object CropsInventory : DrawerNavigationItem("crops_inventory", "Crops & Inventory", Icons.Default.Grass)
    object Market : DrawerNavigationItem("market", "Market", Icons.Default.Storefront)
    object Weather : DrawerNavigationItem("weather", "Weather",Icons.Default.Cloud)
    object Reports : DrawerNavigationItem("reports", "Reports & Analytics", Icons.Default.BarChart)
    object Financials : DrawerNavigationItem("financials", "Financials", Icons.Default.AccountBalanceWallet)
    object FarmerSettings : DrawerNavigationItem("editfarmerprofile", "Settings", Icons.Default.Settings)
    object Help : DrawerNavigationItem("help", "Help & Support", Icons.AutoMirrored.Filled.Help)
}

val UserlistOfDrawerItems = listOf(
    DrawerNavigationItem.EditProfile,
    DrawerNavigationItem.Settings,
    DrawerNavigationItem.AboutUs,
    DrawerNavigationItem.ContactUs
)

val FarmerlistOfDrawerItems = listOf(
    DrawerNavigationItem.Orders,
    DrawerNavigationItem.CropsInventory,
    DrawerNavigationItem.Market,
    DrawerNavigationItem.Weather,
    DrawerNavigationItem.Reports,
    DrawerNavigationItem.Financials,
    DrawerNavigationItem.FarmerSettings,
    DrawerNavigationItem.Help
)

@Composable
fun UserDrawerMenu(
    navController: NavController,
    drawerState: DrawerState,
    closeDrawer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(backgroundColor),
        drawerContainerColor = backgroundColor
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DrawerItem(
                icon = Icons.Default.Agriculture,
                label = "Farmer Account",
                textColor = textColor
            ) {
                scope.launch {
                    client.gotrue.logout()
                    navController.navigate("signinfarmeraccount") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
            UserlistOfDrawerItems.forEach { item ->
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

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()

            DrawerItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp, label = "Log Out",textColor = Color.Red) {
                scope.launch {
                    client.gotrue.logout()
                    navController.navigate("signin") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }
}



@Composable
fun FarmerDrawerMenu(
    navController: NavController,
    drawerState: DrawerState,
    closeDrawer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.secondary

    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(backgroundColor),
        drawerContainerColor = backgroundColor
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DrawerItem(
                icon =  Icons.Default.Person, label = "User Account",textColor = textColor) {
                scope.launch {
                    navController.navigate("signin") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }

            FarmerlistOfDrawerItems.forEach { item ->
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

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()

            DrawerItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp, label = "Log Out",textColor = Color.Red) {
                scope.launch {
                    SuparBaseClient.client.gotrue.logout()
                    navController.navigate("signin") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }
}


@Composable
fun DrawerItem(icon: ImageVector, label: String,textColor: Color = MaterialTheme.colorScheme.secondary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color =textColor)
    }
}

@Preview(showBackground = true)
@Composable
fun DrawerMenuPreview() {
    FarmerDrawerMenu(navController = rememberNavController(), drawerState = rememberDrawerState(DrawerValue.Closed)) {}
    UserDrawerMenu(navController = rememberNavController(), drawerState = rememberDrawerState(DrawerValue.Closed)) {}
}
