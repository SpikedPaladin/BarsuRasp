package me.paladin.barsurasp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.paladin.barsurasp.ui.screens.FacultiesScreen
import me.paladin.barsurasp.ui.screens.MainScreen
import me.paladin.barsurasp.ui.screens.SettingsScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier
    ) {
        composable("main") {
            MainScreen(
                navigateToSettings = { navController.navigate("settings") },
                openFaculties = { navController.navigate("faculties") }
            )
        }
        composable("faculties") {
            FacultiesScreen(updateMainGroup = true) {
                navController.popBackStack("main", false)
            }
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}