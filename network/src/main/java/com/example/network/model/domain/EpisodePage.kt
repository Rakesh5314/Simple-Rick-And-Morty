package com.example.network.model.domain


data class EpisodePage(
    val info : Info,
    val episodes : List<Episode>
){
    data class Info(
        val count : Int,
        val page : Int,
        val next : String?,
        val prev : String?
    )
}
