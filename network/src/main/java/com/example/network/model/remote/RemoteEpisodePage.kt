package com.example.network.model.remote

import com.example.network.model.domain.EpisodePage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteEpisodePage(
    val info : Info,
    @SerialName("results")
    val result : List<RemoteEpisode>
){
    @Serializable
    data class Info(
        val count : Int,
        val pages : Int,
        val next : String?,
        val prev : String?
    )
}

fun RemoteEpisodePage.toDomainEpisodePage(): EpisodePage{
    return EpisodePage(
        info = EpisodePage.Info(
            count = info.count,
            page = info.pages,
            next = info.next,
            prev = info.prev
        ),
        episodes = result.map { it.toDomainEpisode() }
    )
}
