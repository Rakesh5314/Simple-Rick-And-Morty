package com.example.rickandmortybaseapp.component.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.network.model.domain.CharacterStatus
import com.example.rickandmortybaseapp.component.commons.CharacterNameComponent

@Composable
fun CharacterDetailsNamePlateComponent(name : String , status : CharacterStatus){
    Column(modifier = Modifier.fillMaxWidth()){
        CharacterStatusComponent(status)
        CharacterNameComponent(name)
    }
}

@Preview
@Composable
fun NamePlatePreviewAlive() {
    CharacterDetailsNamePlateComponent(name = "Rick Sanchez", status = CharacterStatus.Alive)
}

@Preview
@Composable
fun NamePlatePreviewDead() {
    CharacterDetailsNamePlateComponent(name = "Rick Sanchez", status = CharacterStatus.Dead)
}

@Preview
@Composable
fun NamePlatePreviewUnknown() {
    CharacterDetailsNamePlateComponent(name = "Rick Sanchez", status = CharacterStatus.Unknown)
}