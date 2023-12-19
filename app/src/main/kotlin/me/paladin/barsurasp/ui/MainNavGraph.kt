package me.paladin.barsurasp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.paladin.barsurasp.ui.screens.BusConfigScreen
import me.paladin.barsurasp.ui.screens.BusInfoScreen
import me.paladin.barsurasp.ui.screens.BusListScreen
import me.paladin.barsurasp.ui.screens.BusPathScreen
import me.paladin.barsurasp.ui.screens.BusStopScreen
import me.paladin.barsurasp.ui.screens.ItemsScreen
import me.paladin.barsurasp.ui.screens.MainScreen
import me.paladin.barsurasp.ui.screens.SettingsScreen
import me.paladin.barsurasp.ui.viewmodels.BusPathViewModel
import me.paladin.barsurasp.ui.viewmodels.BusesViewModel
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.SettingsViewModel

@Composable
fun MainNavGraph(
    mainViewModel: MainViewModel,
    busesViewModel: BusesViewModel,
    busPathViewModel: BusPathViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val backAction: () -> Unit = { navController.navigateUp() }

    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier
    ) {
        composable("main") {
            MainScreen(
                mainViewModel,
                busesViewModel,
                viewModelStoreOwner,
                navigateToSettings = { navController.navigate("settings") },
                openFaculties = { navController.navigate("faculties") },
                openBusConfig = { navController.navigate("busConfig") }
            )
        }
        composable("faculties") {
            val savedItems by mainViewModel.savedItems.collectAsState()
            ItemsScreen(
                savedItems = savedItems,
                itemSaved = { mainViewModel.saveItem(it) },
                itemSelected = { item, saveName ->
                    mainViewModel.setMainGroup(item)

                    if (mainViewModel.savedItems.value.isEmpty())
                        mainViewModel.saveItem(saveName)

                    backAction()
                },
                backAction = backAction
            )
        }
        composable("settings") {
            SettingsScreen(settingsViewModel, backAction = backAction)
        }
        composable("busConfig") {
            BusConfigScreen(
                busesViewModel,
                openPathCreate = {
                    busPathViewModel.clear()
                    navController.navigate("busPath")
                },
                backAction = backAction,
                openBusList = { navController.navigate("busList?choosePath=false") }
            )
        }
        composable("busPath") {
            BusPathScreen(
                busPathViewModel,
                openBusChoose = { navController.navigate("busList?choosePath=true") },
                backAction = backAction
            ) {
                busesViewModel.addPath(it)
                navController.navigateUp()
                busPathViewModel.clear()
            }
        }
        composable(
            "busList?choosePath={flag}",
            arguments = listOf(navArgument("flag") { type = NavType.BoolType })
        ) { backStackEntry ->
            val choosePath = backStackEntry.arguments?.getBoolean("flag")

            BusListScreen(
                busesViewModel,
                viewModelStoreOwner,
                backAction = backAction
            ) {
                navController.navigate("busInfo?number=$it&choosePath=${choosePath ?: false}")
            }
        }
        composable(
            "busInfo?number={number}&choosePath={flag}",
            arguments = listOf(
                navArgument("number") { type = NavType.IntType },
                navArgument("flag") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val number = backStackEntry.arguments?.getInt("number")
            val choosePath = backStackEntry.arguments?.getBoolean("flag")

            number?.let {
                BusInfoScreen(
                    it,
                    viewModelStoreOwner,
                    backAction = backAction
                ) { stop, backward ->
                    if (choosePath == true) {
                        busPathViewModel.addPath(Triple(number, stop, backward))
                        navController.popBackStack("busPath", false)
                    } else
                        navController.navigate("busStop?number=$number&stop=$stop&backward=$backward")
                }
            }
        }
        composable(
            "busStop?number={number}&stop={stop}&backward={backward}",
            arguments = listOf(
                navArgument("number") { type = NavType.IntType },
                navArgument("stop") { type = NavType.StringType },
                navArgument("backward") { type = NavType.BoolType }
            )
        ) { backStackEntry ->
            val number = backStackEntry.arguments?.getInt("number")
            val stop = backStackEntry.arguments?.getString("stop")
            val backward = backStackEntry.arguments?.getBoolean("backward")

            number?.let { backward?.let { stop?.let {
                BusStopScreen(
                    number = number,
                    stopName = stop,
                    backward = backward,
                    viewModelStoreOwner = viewModelStoreOwner,
                    backAction = backAction
                )
            }}}
        }
    }
}