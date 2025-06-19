package com.example.rickandmortybaseapp.viewModel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.network.model.domain.Character
import com.example.rickandmortybaseapp.repository.CharacterRepository
import kotlinx.coroutines.flow.update

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) :ViewModel(){
    val searchTextFieldState = TextFieldState()

    sealed interface SearchState {
        object Empty : SearchState
        data class UserQuery(val query : String): SearchState
    }

    sealed interface ScreenState{
        object Empty : ScreenState
        object Searching : ScreenState
        data class Error(val message : String) : ScreenState
        data class Content(val userQuery : String , val result : List<Character>) : ScreenState
    }

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    val uiState = _uiState.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchTextState = snapshotFlow { searchTextFieldState.text }
        .debounce(500)
        .mapLatest { if (it.isBlank()) SearchState.Empty else SearchState.UserQuery(it.toString()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 2000),
            initialValue = SearchState.Empty
        )

    fun observeSearch() = viewModelScope.launch {
        searchTextState.collectLatest { searchState ->
            when(searchState){
                SearchState.Empty -> _uiState.update { ScreenState.Empty }
                is SearchState.UserQuery -> searchAllCharacter(query = searchState.query)
            }
        }
    }

    private fun searchAllCharacter(query : String) = viewModelScope.launch {
        _uiState.update { ScreenState.Searching }
        characterRepository.fetchAllCharacterByName(searchQuery = query).onSuccess { characters ->
            _uiState.update {
                ScreenState.Content(
                    userQuery = query,
                    result = characters
                )
            }
        }.onFailure {exception ->
            _uiState.update { ScreenState.Error(message = exception.message ?: "unknown error" ) }
        }
    }
}