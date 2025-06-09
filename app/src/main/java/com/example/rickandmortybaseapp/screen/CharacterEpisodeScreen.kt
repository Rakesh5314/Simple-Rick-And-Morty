package com.example.rickandmortybaseapp.screen


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.model.domain.Character
import com.example.network.KtorClient
import com.example.network.model.domain.Episode
import com.example.rickandmortybaseapp.component.commons.CharacterImage
import com.example.rickandmortybaseapp.component.commons.CharacterNameComponent
import com.example.rickandmortybaseapp.component.commons.LoadingState
import com.example.rickandmortybaseapp.components.DataPoint
import com.example.rickandmortybaseapp.components.DataPointComponent
import com.example.rickandmortybaseapp.components.episode.EpisodeRowComponent
import com.example.rickandmortybaseapp.ui.theme.RickPrimary
import com.example.rickandmortybaseapp.ui.theme.RickTextPrimary
import kotlinx.coroutines.launch

@Composable
fun CharacterEpisodeScreen(characterId: Int, ktorClient: KtorClient) {
    var characterState by remember { mutableStateOf<Character?>(null) }
    var episodeState by remember { mutableStateOf<List<Episode>>(emptyList()) }


    LaunchedEffect(key1 = null, block = {
        ktorClient.getCharacter(characterId).onSuccess { character ->
            characterState = character
            launch {
                ktorClient.getEpisodes(character.episodeIds).onSuccess { episodes ->
                    episodeState = episodes
                }.onFailure {
                    //TODO
                }
            }
        }.onFailure {
            //TODO
        }
    })

    characterState?.let { character ->
        MainScreen(character = character, episodes = episodeState)
    } ?: LoadingState()
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(character: Character, episodes: List<Episode>) {

    val episodeBySeasonMap = episodes.groupBy { it.seasonNumber }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item { CharacterNameComponent(name = character.name) }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            LazyRow {
                episodeBySeasonMap.forEach{mapEntry ->
                    val title = "Season ${mapEntry.key}"
                    val description = "${mapEntry.value.size} ep"
                    item {
                        DataPointComponent(
                            dataPoint = DataPoint(
                                title = title,
                                description = description
                            )
                        )
                        Spacer(modifier = Modifier.width(32.dp))
                    }
                }
            }
        }
        item { CharacterImage(imageUrl = character.imageUrl) }
        item { Spacer(modifier = Modifier.height(32.dp)) }

        episodeBySeasonMap.forEach{mapEntry ->
            stickyHeader { GroupHeader(seasonNumber = mapEntry.key) }
            items(mapEntry.value){episode ->
                EpisodeRowComponent(episode)
            }
        }
    }
}




@Composable
private fun GroupHeader(seasonNumber : Int){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(color = RickPrimary)
            .padding(top = 8.dp, bottom = 16.dp)
    ){
        Text(
            text = "Season $seasonNumber",
            color = RickTextPrimary,
            fontSize = 32.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = RickTextPrimary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 4.dp)
        )
    }
}
