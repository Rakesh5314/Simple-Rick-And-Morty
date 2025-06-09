package com.example.rickandmortybaseapp.component.Character

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.model.domain.CharacterStatus
import com.example.rickandmortybaseapp.ui.theme.RickAndMortyBaseAppTheme
import com.example.rickandmortybaseapp.ui.theme.RickTextPrimary

@Composable
fun CharacterStatusComponent(characterStatus : CharacterStatus){
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = characterStatus.color,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp , vertical = 4.dp)
    ) {
        Text("Status : ${characterStatus.status}" , fontSize = 20.sp , color = RickTextPrimary)
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewAlive() {
    RickAndMortyBaseAppTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Alive)
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewDead() {
    RickAndMortyBaseAppTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Dead)
    }
}

@Preview
@Composable
fun CharacterStatusComponentPreviewUnknown() {
    RickAndMortyBaseAppTheme {
        CharacterStatusComponent(characterStatus = CharacterStatus.Unknown)
    }
}