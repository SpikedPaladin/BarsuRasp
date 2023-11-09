package me.paladin.barsurasp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.paladin.barsurasp.ui.screens.FacultiesScreen
import me.paladin.barsurasp.ui.screens.MainScreen
import me.paladin.barsurasp.ui.screens.SettingsScreen
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.SettingsViewModel

@Composable
fun MainNavGraph(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
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
                mainViewModel,
                navigateToSettings = { navController.navigate("settings") },
                openFaculties = { navController.navigate("faculties") }
            )
        }
        composable("faculties") {
            FacultiesScreen(mainViewModel, updateMainGroup = true) {
                navController.popBackStack("main", false)
            }
        }
        composable("settings") {
            SettingsScreen(settingsViewModel)
        }
    }
}