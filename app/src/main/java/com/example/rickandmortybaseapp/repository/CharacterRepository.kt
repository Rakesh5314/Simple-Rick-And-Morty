package com.example.rickandmortybaseapp.repository

import com.example.network.ApiOperation
import com.example.network.KtorClient
import com.example.network.model.domain.Character
import com.example.network.model.domain.CharacterPage
import javax.inject.Inject

class CharacterRepository @Inject constructor(private val ktorClient: KtorClient) {

    suspend fun fetchCharacterByPage(page : Int) : ApiOperation<CharacterPage>{
        return ktorClient.getCharacterByPage(pageNumber = page)
    }

    suspend fun fetchCharacter(characterId: Int) : ApiOperation<Character> {
        return ktorClient.getCharacter(characterId)
    }
}