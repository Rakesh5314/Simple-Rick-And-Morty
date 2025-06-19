package com.example.rickandmortybaseapp.component.character

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.SubcomposeAsyncImage
import com.example.network.model.domain.Character
import com.example.rickandmortybaseapp.component.commons.LoadingState
import com.example.rickandmortybaseapp.component.commons.SimpleToolbar
import com.example.rickandmortybaseapp.components.DataPoint
import com.example.rickandmortybaseapp.components.DataPointComponent
import com.example.rickandmortybaseapp.repository.CharacterRepository
import com.example.rickandmortybaseapp.ui.theme.RickAction
import com.example.rickandmortybaseapp.viewModel.CharacterDetailViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



sealed interface CharacterDetailViewState{
    object Loading : CharacterDetailViewState
    data class Error(val message : String) : CharacterDetailViewState
    data class Success(val character : Character, val characterDataPoints : List<DataPoint>) :
        CharacterDetailViewState
}

@Composable
fun CharacterDetailScreen(
    characterId: Int,
    viewModel: CharacterDetailViewModel = hiltViewModel(),
    onEpisodeClicked: (Int) -> Unit,
    onBackClicked : () -> Unit
) {
    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.fetchCharacter(characterId = characterId)
        }
    )

    val state by viewModel.stateFlow.collectAsState()

    Column {
        SimpleToolbar(
            title = "Character Details",
            onBackAction = onBackClicked
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            when (val viewState = state) {
                CharacterDetailViewState.Loading -> item { LoadingState() }
                is CharacterDetailViewState.Success -> {
                    item {
                        CharacterDetailsNamePlateComponent(
                            name = viewState.character.name,
                            status = viewState.character.status
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SubcomposeAsyncImage(
                            model = viewState.character.imageUrl,
                            contentDescription = "character image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp)),
                            loading = { LoadingState() }
                        )
                    }

                    items(viewState.characterDataPoints) {
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
                is CharacterDetailViewState.Error -> {}
                else -> {}
            }
        }
    }
}