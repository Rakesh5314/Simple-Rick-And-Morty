package com.example.network.model.domain

data class CharacterPage(
    val info : Info,
    val character : List<Character>
){
    data class Info(
        val count : Int,
        val page : Int,
        val next : String?,
        val prev : String?
    )
}
