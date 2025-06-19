package com.example.network

import com.example.network.model.domain.Character
import com.example.network.model.domain.CharacterPage
import com.example.network.model.domain.Episode
import com.example.network.model.domain.EpisodePage
import com.example.network.model.remote.RemoteCharacter
import com.example.network.model.remote.RemoteCharacterPage
import com.example.network.model.remote.RemoteEpisode
import com.example.network.model.remote.RemoteEpisodePage
import com.example.network.model.remote.toDomainCharacter
import com.example.network.model.remote.toDomainCharacterPage
import com.example.network.model.remote.toDomainEpisode
import com.example.network.model.remote.toDomainEpisodePage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class KtorClient {
    private val client = HttpClient(OkHttp) {
        defaultRequest { url("https://rickandmortyapi.com/api/") }

        install(Logging) {
            logger = Logger.SIMPLE
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private var characterCache = mutableMapOf<Int, Character>()

    suspend fun getCharacter(id: Int): ApiOperation<Character> {

        return safeApiCall {
            characterCache[id]?.let { return ApiOperation.Success(it) }
            client.get("character/${id}")
                .body<RemoteCharacter>()
                .toDomainCharacter()
                .also { characterCache[id] = it }
        }
    }

    suspend fun getCharacterByPage(pageNumber : Int , queryParam : Map<String , String>) : ApiOperation<CharacterPage>{
        return safeApiCall {
            client.get("character"){
                url {
                    parameters.append("page" , pageNumber.toString())
                    queryParam.forEach { parameters.append(it.key,it.value) }
                }
            }
                .body<RemoteCharacterPage>()
                .toDomainCharacterPage()
        }
    }

    suspend fun searchAllCharacterByName(searchQuery : String) : ApiOperation<List<Character>>{
        val data = mutableListOf<Character>()
        var exception : Exception ?= null

        getCharacterByPage(
            pageNumber = 1,
            queryParam = mapOf("name" to searchQuery)
        ).onSuccess { firstPage ->
            val totalPageCount = firstPage.info.page
            data.addAll(firstPage.character)

            repeat(totalPageCount - 1){index ->
                getCharacterByPage(
                    pageNumber = 1,
                    queryParam = mapOf("name" to searchQuery)
                ).onSuccess { nextPage ->
                    data.addAll(nextPage.character)
                }.onFailure { error ->
                    exception = error
                }
                if(exception == null){ return@onSuccess }
            }
        }.onFailure {
            exception = it
        }
        return exception ?. let { ApiOperation.Failure(it) } ?: ApiOperation.Success(data)
    }

    suspend fun getEpisode(episodeId : Int) : ApiOperation<Episode>{
        return safeApiCall {
            client.get("episode/$episodeId")
                .body<RemoteEpisode>()
                .toDomainEpisode()
        }
    }

    suspend fun getEpisodes(episodeIds: List<Int>): ApiOperation<List<Episode>> {
        return if(episodeIds.size == 1){
            getEpisode(episodeIds[0]).mapSuccess {
                listOf(it)
            }
        }else{
            val idsCommaSeparated = episodeIds.joinToString(separator = ",")
            safeApiCall {
                client.get("episode/$idsCommaSeparated")
                    .body<List<RemoteEpisode>>()
                    .map { it.toDomainEpisode() }
            }
        }
    }

    suspend fun getEpisodeByPage(pageIndex : Int) : ApiOperation<EpisodePage>{
        return safeApiCall {
            client.get("episode"){
                url {
                    parameters.append("page", pageIndex.toString())
                }
            }
                .body<RemoteEpisodePage>()
                .toDomainEpisodePage()
        }
    }

    suspend fun getAllEpisodes() : ApiOperation<List<Episode>>{
        val data =  mutableListOf<Episode>()
        var exception : Exception ?= null

        getEpisodeByPage(pageIndex = 1).onSuccess { firstPage ->
            val totalPageCount = firstPage.info.page
            data.addAll(firstPage.episodes)

            repeat(totalPageCount-1){index ->
                getEpisodeByPage(index + 2).onSuccess {nextpage ->
                    data.addAll(nextpage.episodes)
                }.onFailure { error ->
                    exception = error
                }
                if (exception == null) {return@onSuccess}
            }
        }.onFailure { exception = it }
        return exception?.let { ApiOperation.Failure(it) } ?: ApiOperation.Success(data)
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): ApiOperation<T> {
        return try {
            ApiOperation.Success(data = apiCall())
        } catch (e: Exception) {
            ApiOperation.Failure(exception = e)
        }
    }
}

sealed interface ApiOperation<T> {
    data class Success<T>(val data: T) : ApiOperation<T>
    data class Failure<T>(val exception: Exception) : ApiOperation<T>

    fun <R> mapSuccess(transform : (T) -> R) : ApiOperation<R>{
        return when(this){
            is Success -> Success(transform(data))
            is Failure -> Failure(exception)
        }
    }

    suspend fun onSuccess(block: suspend (T) -> Unit): ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): ApiOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}


@Serializable
data class Character(
    val id: Int,
    val name: String,
    val origin: Origin
) {
    @Serializable
    data class Origin(
        val name: String
    )
}