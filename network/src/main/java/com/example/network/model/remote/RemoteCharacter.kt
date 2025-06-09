package com.example.network.model.remote

import com.example.network.model.domain.Character
import com.example.network.model.domain.CharacterGender
import com.example.network.model.domain.CharacterStatus
import kotlinx.serialization.Serializable

@Serializable
data class RemoteCharacter(
    val created : String,
    val episode : List<String>,
    val gender : String,
    val id : Int,
    val image : String,
    val location : Location,
    val name : String,
    val origin : Origin,
    val species : String,
    val status : String,
    val type :String,
    val url : String
){
    @Serializable
    data class Location(
        val name: String,
        val url: String
    )

    @Serializable
    data class Origin(
        val name: String,
        val url : String
    )
}

fun RemoteCharacter.toDomainCharacter() : Character{
    val characterGender = when(gender.lowercase()){
        "male" -> CharacterGender.Male
        "female" -> CharacterGender.Female
        "genderless" -> CharacterGender.GenderLess
        else -> CharacterGender.Unknown
    }

    val characterStatus = when(status.lowercase()){
        "alive" -> CharacterStatus.Alive
        "dead" -> CharacterStatus.Dead
        else -> CharacterStatus.Unknown
    }

    return Character(
        created = created,
        episodeIds = episode.map { it.substring(it.lastIndexOf("/")+1).toInt() },
        gender = characterGender,
        id = id,
        imageUrl = image,
        location = Character.Location(name = location.name , url = location.url),
        name = name,
        origin = Character.Origin(name = origin.name , url = origin.url),
        species = species,
        status = characterStatus,
        type = type,
    )
}