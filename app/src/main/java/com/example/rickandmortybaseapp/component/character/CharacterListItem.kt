package com.example.rickandmortybaseapp.component.character

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.network.model.domain.Character
import com.example.rickandmortybaseapp.component.commons.CharacterImage
import com.example.rickandmortybaseapp.components.DataPoint
import com.example.rickandmortybaseapp.components.DataPointComponent
import com.example.rickandmortybaseapp.ui.theme.RickAction

@Composable
fun CharacterListItem(
    character : Character,
    modifier: Modifier = Modifier,
    characterDataPoint: List<DataPoint>,
    onClick : () -> Unit
){
    Row (
        modifier = modifier
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(Color.Transparent, RickAction)),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .clip(shape = RoundedCornerShape(12.dp))
    ){
        Box{
            CharacterImage(
                imageUrl = character.imageUrl,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            )
            CharacterStatusCircle(
                status = character.status,
                modifier = Modifier
                    .padding(start = 6.dp, top = 6.dp)
            )
        }

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2) ,
            contentPadding = PaddingValues(start = 16.dp , end = 16.dp),
            content = {
            items(
                items = listOf(
                    DataPoint(
                        title = "name",
                        description = character.name
                    ),
                ) + characterDataPoint,
                key = {it.hashCode()}
            ){dataPoint ->
                Column(verticalArrangement = Arrangement.Center , modifier = Modifier.padding(end = 16.dp)) {
                    DataPointComponent(dataPoint = dataPoint)
                }
            }
        })
    }
}

private fun sanitizeDataPoints(dataPoint: DataPoint) : DataPoint{
    val newDescription = if(dataPoint.description.length > 14){
        dataPoint.description.take(12)+".."
    }else{
        dataPoint.description
    }
    return dataPoint.copy(description = newDescription)
}