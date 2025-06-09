package com.example.rickandmortybaseapp.component.commons

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rickandmortybaseapp.ui.theme.RickAction

val defaultModifier = Modifier
    .fillMaxSize()
    .padding(128.dp)

@Composable
fun LoadingState(modifier: Modifier = defaultModifier){
    CircularProgressIndicator(
        modifier = modifier,
        color = RickAction
    )
}