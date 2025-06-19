package com.example.rickandmortybaseapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortybaseapp.component.character.CharacterDetailViewState
import com.example.rickandmortybaseapp.components.DataPoint
import com.example.rickandmortybaseapp.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
) : ViewModel(){
    private val _internalStorageFlow = MutableStateFlow<CharacterDetailViewState>(
        value = CharacterDetailViewState.Loading
    )

    val stateFlow = _internalStorageFlow.asStateFlow()

    fun fetchCharacter(characterId: Int) = viewModelScope.launch {

        _internalStorageFlow.update { CharacterDetailViewState.Loading }

        characterRepository.fetchCharacter(characterId).onSuccess { character ->
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

            _internalStorageFlow.update {
                return@update CharacterDetailViewState.Success(
                    character = character,
                    characterDataPoints = dataPoints
                )
            }

        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update CharacterDetailViewState.Error(
                    message = exception.message ?: "unknown error occured"
                )
            }
        }
    }
}