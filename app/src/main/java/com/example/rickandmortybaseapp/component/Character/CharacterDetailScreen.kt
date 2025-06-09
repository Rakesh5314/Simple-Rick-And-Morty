package com.example.rickandmortybaseapp.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.network.model.domain.Character
import com.example.network.KtorClient
import com.example.rickandmortybaseapp.component.Character.CharacterDetailsNamePlateComponent
import com.example.rickandmortybaseapp.component.commons.LoadingState
import com.example.rickandmortybaseapp.components.DataPoint
import com.example.rickandmortybaseapp.components.DataPointComponent
import com.example.rickandmortybaseapp.ui.theme.RickAction
import kotlinx.coroutines.delay

@Composable
fun CharacterDetailScreen  (
    ktorClient: KtorClient,
    characterId : Int,
    onEpisodeClicked : (Int) -> Unit
){
    var character by remember { mutableStateOf<Character?>(null) }

    val characterDataPoints : List<DataPoint> by remember {
        derivedStateOf {
            buildList {
                character ?. let { character ->
                    add(DataPoint("last known location" , character.location.name))
                    add(DataPoint("species" , character.species))
                    add(DataPoint("gender" , character.gender.displayName))
                    character.type.takeIf { it.isNotEmpty() }?.let {type ->
                        add(DataPoint("type" , type))
                    }
                    add(DataPoint("Origin" , character.origin.name))
                    add(DataPoint("episode count" , character.episodeIds.size.toString()))
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        //delay(500)
        ktorClient
            .getCharacter(characterId)
            .onSuccess {
                character = it
            }.onFailure { exception ->
                //Todo handle Exception
            }
    })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        if (character == null){
            item { LoadingState() }
            return@LazyColumn
        }

        item {
            CharacterDetailsNamePlateComponent(
                name = character!!.name,
                status = character!!.status
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            SubcomposeAsyncImage (
                model = character!!.imageUrl,
                contentDescription = "character image",
                modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)),
                loading = { LoadingState() }
            )
        }

        items(characterDataPoints){
            Spacer(modifier = Modifier.height(32.dp))
            DataPointComponent(dataPoint = it)
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Button(onClick = {}) {
                Text(
                    text = "View All episodes",
                    color = RickAction,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .border(
                            width = 1.dp,
                            color = RickAction,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            onEpisodeClicked(characterId)
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}