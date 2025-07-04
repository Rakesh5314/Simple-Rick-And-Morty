package com.example.rickandmortybaseapp.component.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

private val defaultModi = Modifier
    .fillMaxWidth()
    .aspectRatio(1f)
    .clip(RoundedCornerShape(12.dp))

@Composable
fun CharacterImage(imageUrl : String , modifier: Modifier = defaultModi){
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = "Character image",
        modifier = modifier,
        loading = { LoadingState() }
    )
}

@Preview
@Composable
private fun CharacterImagePreview() {
    CharacterImage(
        modifier = defaultModifier.then(
            Modifier.background(
                brush = Brush.verticalGradient(listOf(Color.White, Color.Black))
            )
        ),
        imageUrl = "image url"
    )
}