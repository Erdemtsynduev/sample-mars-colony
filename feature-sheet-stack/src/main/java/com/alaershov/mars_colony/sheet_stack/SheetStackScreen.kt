@file:OptIn(ExperimentalMaterial3Api::class)

package com.alaershov.mars_colony.sheet_stack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.sheet_stack.component.PreviewSheetStackComponent
import com.alaershov.mars_colony.sheet_stack.component.SheetStackComponent
import com.alaershov.mars_colony.ui.R
import com.alaershov.mars_colony.ui.theme.MarsColonyTheme

@Composable
fun SheetStackScreen(component: SheetStackComponent) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Bottom Sheet Stack",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        // TODO
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
            )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "TODO сделать тестовый экран для стека БШ\n" +
                        "!!! DraggableCards вдохновение !!!\n" +
                        "- вставка на верхушку стека\n" +
                        "- удаление с верхушки\n" +
                        "- вставка в начало или в середину\n" +
                        "- множественная вставка\n" +
                        "- множественное удаление\n" +
                        "- swap",
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Preview(device = "id:pixel_9")
@Composable
private fun HabitatListScreenPreview() {
    MarsColonyTheme {
        SheetStackScreen(PreviewSheetStackComponent())
    }
}
