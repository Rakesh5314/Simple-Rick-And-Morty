package com.example.rickandmortybaseapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortybaseapp.repository.EpisodeRepository
import com.example.rickandmortybaseapp.screen.AllEpisodesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllEpisodeViewModel @Inject constructor(
    private val episodeRepository: EpisodeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AllEpisodesUiState>(AllEpisodesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun refreshAllEpisodes(forceRefresh : Boolean = false) = viewModelScope.launch {
        if (forceRefresh) _uiState.update { AllEpisodesUiState.Loading }
        episodeRepository.fetchAllEpisodes().onSuccess {episodeList ->
            _uiState.update {
                AllEpisodesUiState.Success(
                    data = episodeList.groupBy {
                        it.seasonNumber.toString()
                    }.mapKeys {
                        "Season ${it.key}"
                    }
                )
            }

        }.onFailure { AllEpisodesUiState.Error }
    }
}