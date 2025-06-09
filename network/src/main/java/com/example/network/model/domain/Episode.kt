package com.example.network.model.domain

data class Episode(
    val id : Int,
    val name : String,
    val seasonNumber : Int,
    val episodeNumber : Int,
    val air_date : String,
    val characterIdsInEpisode : List<Int>
)
