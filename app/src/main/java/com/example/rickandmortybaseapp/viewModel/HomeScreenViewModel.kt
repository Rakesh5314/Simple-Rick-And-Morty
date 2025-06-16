package com.example.rickandmortybaseapp.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.model.domain.CharacterPage
import com.example.rickandmortybaseapp.repository.CharacterRepository
import com.example.rickandmortybaseapp.screen.HomeScreenViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel() {
    private val _viewState = MutableStateFlow<HomeScreenViewState>(HomeScreenViewState.Loading)
    val viewState: StateFlow<HomeScreenViewState> = _viewState.asStateFlow()

    private val fetchedCharacterPages = mutableListOf<CharacterPage>()

    fun fetchInitialPage() = viewModelScope.launch {
        if (fetchedCharacterPages.isNotEmpty()) return@launch
        val initialPage = characterRepository.fetchCharacterByPage(page = 1)
        initialPage.onSuccess { characterPage ->

            fetchedCharacterPages.clear()
            fetchedCharacterPages.add(characterPage)

            _viewState.update { return@update HomeScreenViewState.GridDisplay(characters = characterPage.character) }
        }.onFailure { }
    }

    fun fetchNextPage() = viewModelScope.launch {
        val nextPageIndex = fetchedCharacterPages.size+1
        characterRepository.fetchCharacterByPage(nextPageIndex).onSuccess {characterPage ->
            fetchedCharacterPages.add(characterPage)
            _viewState.update { currenntState ->
                val currentCharacter = (currenntState as? HomeScreenViewState.GridDisplay)?.characters?: emptyList()
                return@update HomeScreenViewState.GridDisplay(characters = currentCharacter + characterPage.character)
            }
        }
    }
}