package com.example.network.model.domain

import androidx.compose.ui.graphics.Color

sealed class CharacterStatus(val status : String , val color: Color) {
    object Alive : CharacterStatus("Alive" , color = Color.Green)
    object Dead : CharacterStatus("Dead" , color = Color.Red)
    object Unknown : CharacterStatus("Unknown" , color = Color.Yellow)
}