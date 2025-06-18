package com.example.rickandmortybaseapp.repository

import com.example.network.ApiOperation
import com.example.network.KtorClient
import com.example.network.model.domain.Episode
import javax.inject.Inject

class EpisodeRepository @Inject constructor(
    private val ktorClient: KtorClient
) {
    suspend fun fetchAllEpisodes() : ApiOperation<List<Episode>> = ktorClient.getAllEpisodes()
}