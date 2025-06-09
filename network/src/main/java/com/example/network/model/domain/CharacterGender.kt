package com.example.network.model.domain

sealed class CharacterGender(val displayName : String) {
    object Male : CharacterGender("Male")
    object Female : CharacterGender("Female")
    object GenderLess : CharacterGender("No Gender")
    object Unknown : CharacterGender("Unknown")
}