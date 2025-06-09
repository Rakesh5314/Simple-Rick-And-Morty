package com.example.network.model.remote

import com.example.network.model.domain.Episode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteEpisode(
    @SerialName("id")
    val id : Int,
    @SerialName("name")
    val name : String,
    @SerialName("episode")
    val episode : String,
    @SerialName("air_date")
    val air_date : String,
    @SerialName("characters")
    val character: List<String>
)

fun RemoteEpisode.toDomainEpisode() : Episode {
    return Episode(
        id = id,
        name = name,
        seasonNumber = episode.filter { it.isDigit() }.take(2).toInt(),
        episodeNumber = episode.filter { it.isDigit() }.takeLast(2).toInt(),
        air_date = air_date,
        characterIdsInEpisode = character.map {
            it.substring(startIndex = it.lastIndexOf("/")+1).toInt()
        }
    )
}
