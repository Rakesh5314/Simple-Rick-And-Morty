package com.example.rickandmortybaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.network.KtorClient
import com.example.rickandmortybaseapp.component.character.CharacterDetailScreen
import com.example.rickandmortybaseapp.screen.AllEpisodeScreen
import com.example.rickandmortybaseapp.screen.CharacterEpisodeScreen
import com.example.rickandmortybaseapp.screen.HomeScreen
import com.example.rickandmortybaseapp.screen.SearchScreen
import com.example.rickandmortybaseapp.ui.theme.RickAction
import com.example.rickandmortybaseapp.ui.theme.RickAndMortyBaseAppTheme
import com.example.rickandmortybaseapp.ui.theme.RickPrimary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

sealed class NavDetination(val title : String, val route : String, val icon : ImageVector){
    object Home : NavDetination(title = "Home" , route = "home_screen" , icon = Icons.Filled.Home)
    object Episode : NavDetination(title = "Episode" , route = "episode" , icon = Icons.Filled.PlayArrow)
    object Search : NavDetination(title = "Search" , route = "search" , icon = Icons.Filled.Search)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var ktorClient : KtorClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val items = listOf(
                NavDetination.Home,
                NavDetination.Search,
                NavDetination.Episode
            )
            var selectecIndex by remember { mutableIntStateOf(0) }

            RickAndMortyBaseAppTheme {
                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = RickPrimary) {
                            items.forEachIndexed { index, screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(screen.icon, contentDescription = null)
                                    },
                                    label = { screen.title },
                                    selected = index == selectecIndex,
                                    onClick = {
                                        selectecIndex = index
                                        navController.navigate(screen.route){
                                            popUpTo(navController.graph.findStartDestination().id){
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = RickAction,
                                        selectedTextColor = RickAction,
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                ){innerPadding ->
                    NavHost(navController = navController , startDestination = "home_screen" , modifier = Modifier.background(color = RickPrimary).padding(innerPadding)){
                        composable(route = "home_screen"){
                            HomeScreen(onCharacterSelected = { characterId ->
                                navController.navigate("character_details/$characterId")
                            })
                        }
                        composable(
                            route = "character_details/{characterId}",
                            arguments = listOf(navArgument("characterId"){
                                type = NavType.IntType
                            }
                            )
                        ){backStackEntry ->
                            val characterId : Int = backStackEntry.arguments?.getInt("characterId") ?: -1
                            CharacterDetailScreen(
                                characterId = characterId,
                                onEpisodeClicked = {
                                    navController.navigate("character_episodes/$it")
                                },
                                onBackClicked = {
                                    navController.navigateUp()
                                }
                            )
                        }
                        composable(
                            route = "character_episodes/{characterId}",
                            arguments = listOf(navArgument("characterId"){type = NavType.IntType})
                        ) { backStackEntry->
                            val characterId : Int = backStackEntry.arguments?.getInt("characterId") ?: -1
                            CharacterEpisodeScreen(characterId = characterId, ktorClient = ktorClient , onBackClicked = {navController.navigateUp()})
                        }
                        composable(route = NavDetination.Episode.route){
                            Column(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                AllEpisodeScreen()
                            }
                        }
                        composable(route = NavDetination.Search.route){
                            Column(
                                modifier = Modifier.fillMaxSize(),

                            ) {
                                SearchScreen()
                            }
                        }
                    }
                }
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = RickPrimary
//                ) {
//                    NavHost(navController = navController , startDestination = "home_screen"){
//                        composable(route = "home_screen"){
//                            HomeScreen(onCharacterSelected = { characterId ->
//                                navController.navigate("character_details/$characterId")
//                            })
//                        }
//                        composable(
//                            route = "character_details/{characterId}",
//                            arguments = listOf(navArgument("characterId"){
//                                type = NavType.IntType
//                            }
//                        )
//                        ){backStackEntry ->
//                            val characterId : Int = backStackEntry.arguments?.getInt("characterId") ?: -1
//                            CharacterDetailScreen(
//                                characterId = characterId,
//                                onEpisodeClicked = {
//                                    navController.navigate("character_episodes/$it")
//                                },
//                                onBackClicked = {
//                                    navController.navigateUp()
//                                }
//                            )
//                        }
//                        composable(
//                            route = "character_episodes/{characterId}",
//                            arguments = listOf(navArgument("characterId"){type = NavType.IntType})
//                        ) { backStackEntry->
//                            val characterId : Int = backStackEntry.arguments?.getInt("characterId") ?: -1
//                            CharacterEpisodeScreen(characterId = characterId, ktorClient = ktorClient , onBackClicked = {navController.navigateUp()})
//                        }
//                        composable(route = NavDetination.Episode.route){
//                            Column(
//                                modifier = Modifier.fillMaxSize(),
//                                verticalArrangement = Arrangement.Center,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(text = "Episode" , fontSize = 62.sp , color = Color.White)
//                            }
//                        }
//                        composable(route = NavDetination.Search.route){
//                            Column(
//                                modifier = Modifier.fillMaxSize(),
//                                verticalArrangement = Arrangement.Center,
//                                horizontalAlignment = Alignment.CenterHorizontally
//                            ) {
//                                Text(text = "Search" , fontSize = 62.sp , color = Color.White)
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}