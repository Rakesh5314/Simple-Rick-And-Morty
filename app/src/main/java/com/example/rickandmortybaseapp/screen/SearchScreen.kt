package com.example.rickandmortybaseapp.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortybaseapp.component.character.CharacterListItem
import com.example.rickandmortybaseapp.component.commons.SimpleToolbar
import com.example.rickandmortybaseapp.components.DataPoint
import com.example.rickandmortybaseapp.ui.theme.RickAction
import com.example.rickandmortybaseapp.ui.theme.RickPrimary
import com.example.rickandmortybaseapp.viewModel.SearchViewModel

@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {
    DisposableEffect(key1 = Unit) {
        val job = searchViewModel.observeSearch()
        onDispose { job.cancel() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleToolbar(title = "Search")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = RickPrimary
                )
                BasicTextField(
                    state = searchViewModel.searchTextFieldState,
                    modifier = Modifier.weight(1f)
                )
            }
            AnimatedVisibility(visible = searchViewModel.searchTextFieldState.text.isNotBlank()) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete icon",
                    tint = RickAction,
                    modifier = Modifier.clickable {
                        searchViewModel.searchTextFieldState.edit { delete(0, length) }
                    }
                )
            }
        }
        val screenState by searchViewModel.uiState.collectAsStateWithLifecycle()
        when (val state = screenState) {
            is SearchViewModel.ScreenState.Content -> SearchScreenContent(content = state)
            SearchViewModel.ScreenState.Empty -> {
                Text(
                    text = "search for character",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp
                )
            }

            is SearchViewModel.ScreenState.Error -> {
                Text(
                    text = state.message,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp
                )
            }

            SearchViewModel.ScreenState.Searching -> {}
        }
    }
}

@Composable
private fun SearchScreenContent(content: SearchViewModel.ScreenState.Content) {
    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 16.dp , end = 16.dp , bottom = 24.dp)
    ){
        items(content.result) {character ->
            val dataPoints = buildList {
                add(DataPoint("last known location", character.location.name))
                add(DataPoint("species", character.species))
                add(DataPoint("gender", character.gender.displayName))
                character.type.takeIf { it.isNotEmpty() }?.let { type ->
                    add(DataPoint("type", type))
                }
                add(DataPoint("Origin", character.origin.name))
                add(DataPoint("episode count", character.episodeIds.size.toString()))
            }
            CharacterListItem(
                character = character,
                characterDataPoint =dataPoints,
                onClick = {
                    //TODO
                }
            )
        }
    }
}

