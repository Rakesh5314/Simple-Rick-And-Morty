package com.example.network.model.remote

import com.example.network.model.domain.CharacterPage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCharacterPage(
    val info : Info,
    @SerialName("results")
    val result : List<RemoteCharacter>
){
    @Serializable
    data class Info(
        val count : Int,
        @SerialName("pages")
        val page : Int,
        val next : String?,
        val prev : String?
    )
}

fun RemoteCharacterPage.toDomainCharacterPage() : CharacterPage{
    return CharacterPage(
        info = CharacterPage.Info(
            count = info.count,
            page = info.page,
            next = info.next,
            prev = info.prev
        ),
        character = result.map { it.toDomainCharacter() }
    )
}
