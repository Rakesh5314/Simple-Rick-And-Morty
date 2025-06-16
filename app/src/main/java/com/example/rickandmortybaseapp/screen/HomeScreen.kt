package com.example.rickandmortybaseapp.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.network.model.domain.Character
import com.example.rickandmortybaseapp.component.character.CharacterGridItem
import com.example.rickandmortybaseapp.component.commons.LoadingState
import com.example.rickandmortybaseapp.component.commons.SimpleToolbar
import com.example.rickandmortybaseapp.viewModel.HomeScreenViewModel

sealed interface HomeScreenViewState{
    object Loading : HomeScreenViewState
    data class GridDisplay(
        val characters : List<Character> = emptyList()
    ) : HomeScreenViewState
}



@Composable
fun HomeScreen(
    onCharacterSelected : (Int) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
){
    val viewState by viewModel.viewState.collectAsState()
    LaunchedEffect(key1 = Unit , block = {
        viewModel.fetchInitialPage()
    })

    val scrollState = rememberLazyGridState()
    val fetchNextPage : Boolean by remember {
        derivedStateOf {
            val currentCharacterCount = (viewState as? HomeScreenViewState.GridDisplay)?.characters?.size
                ?:return@derivedStateOf false
            val lastDisplayedIndex = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?:return@derivedStateOf false
            return@derivedStateOf lastDisplayedIndex >= currentCharacterCount - 10
        }
    }

    LaunchedEffect(
        key1 = fetchNextPage,
        block = {
            if (fetchNextPage) viewModel.fetchNextPage()
        }
    )

    when(val state = viewState) {
        HomeScreenViewState.Loading -> LoadingState()
        is HomeScreenViewState.GridDisplay -> {
            Column {
                SimpleToolbar(title = "All Character")
                LazyVerticalGrid(
                    state = scrollState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    columns = GridCells.Fixed(2) ,
                    content ={
                        items(
                            items = state.characters,
                            key = { it.id }
                        ){ character ->
                            CharacterGridItem(
                                modifier = Modifier ,
                                character = character
                            ) {
                                onCharacterSelected(character.id)
                            }
                        }
                    } )
            }
        }
    }
}