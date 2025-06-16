package com.example.rickandmortybaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.network.KtorClient
import com.example.rickandmortybaseapp.component.character.CharacterDetailScreen
import com.example.rickandmortybaseapp.screen.CharacterEpisodeScreen
import com.example.rickandmortybaseapp.screen.HomeScreen
import com.example.rickandmortybaseapp.ui.theme.RickAndMortyBaseAppTheme
import com.example.rickandmortybaseapp.ui.theme.RickPrimary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var ktorClient : KtorClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            RickAndMortyBaseAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    NavHost(navController = navController , startDestination = "home_screen"){
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
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RickAndMortyBaseAppTheme {
        Greeting("Android")
    }
}