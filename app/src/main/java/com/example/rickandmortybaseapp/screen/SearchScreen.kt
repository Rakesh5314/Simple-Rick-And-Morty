package com.example.rickandmortybaseapp.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.network.model.domain.CharacterStatus
import com.example.rickandmortybaseapp.component.character.CharacterListItem
import com.example.rickandmortybaseapp.component.commons.SimpleToolbar
import com.example.rickandmortybaseapp.components.DataPoint
import com.example.rickandmortybaseapp.ui.theme.RickAction
import com.example.rickandmortybaseapp.ui.theme.RickPrimary
import com.example.rickandmortybaseapp.viewModel.SearchViewModel

@Composable
fun SearchScreen(
    onCharacterClicked: (Int) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    DisposableEffect(key1 = Unit) {
        val job = searchViewModel.observeSearch()
        onDispose { job.cancel() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleToolbar(title = "Search")

        val screenState by searchViewModel.uiState.collectAsStateWithLifecycle()
        AnimatedVisibility(visible = screenState is SearchViewModel.ScreenState.Searching) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
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

        when (val state = screenState) {
            is SearchViewModel.ScreenState.Content -> SearchScreenContent(
                content = state,
                onStatusClicked = searchViewModel::toggleStatus,
                onCharacterClicked = { onCharacterClicked(it) }
            )

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
                Button(
                    colors = ButtonDefaults.buttonColors().copy(containerColor = RickAction),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 52.dp),
                    onClick = { searchViewModel.searchTextFieldState.clearText() }
                ) {
                    Text("clear search")
                }
            }

            SearchViewModel.ScreenState.Searching -> {}
        }
    }
}

@Composable
private fun SearchScreenContent(
    content: SearchViewModel.ScreenState.Content,
    onStatusClicked: (CharacterStatus) -> Unit,
    onCharacterClicked : (Int) -> Unit
) {
    Text(
        text = "${content.result.size} results for ${content.userQuery}",
        color = Color.White,
        modifier = Modifier
            .padding(start = 16.dp , bottom = 4.dp),
        fontSize = 14.sp
    )

    Row(
        modifier = Modifier
            .padding(start = 16.dp , end = 16.dp , bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content.filterState.statuses.forEach { status ->
            val isSelected = content.filterState.selectedStatuses.contains(status)
            val contentColor = if (isSelected) RickAction else Color.LightGray
            val count = content.result.filter { it.status == status }.size
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = contentColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        onStatusClicked(status)
                    }
                    .clip(RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = count.toString(),
                    color = RickPrimary,
                    modifier = Modifier
                        .background(color = contentColor)
                        .padding(4.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = status.status,
                    color = contentColor,
                    modifier = Modifier
                        .padding(horizontal = 6.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Box {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            val filteredResults = content.result
                .filter { content.filterState.selectedStatuses.contains(it.status) }
                .distinctBy { it.id }

            items(
                items = filteredResults,
                key = {character -> character.id}
            ) { character ->
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
                    characterDataPoint = dataPoints,
                    onClick = {
                        onCharacterClicked(character.id)
                    },
//                    modifier = Modifier.animateItem()
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            RickPrimary,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

