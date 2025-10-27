@file:OptIn(ExperimentalMaterial3Api::class)

package com.alaershov.mars_colony.sheet_stack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.bottom_sheet.material3.pages.ChildPagesModalBottomSheet
import com.alaershov.mars_colony.sheet_stack.bottom_sheet.SheetStackBottomSheetContent
import com.alaershov.mars_colony.sheet_stack.component.PreviewSheetStackComponent
import com.alaershov.mars_colony.sheet_stack.component.SheetStackComponent
import com.alaershov.mars_colony.ui.R
import com.alaershov.mars_colony.ui.theme.MarsColonyTheme
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun SheetStackScreen(component: SheetStackComponent) {
    Box {
        ScreenContent(component)

        ChildPagesModalBottomSheet(
            sheetContentPagesState = component.bottomSheetPages,
            onDismiss = component::onBottomSheetPagesDismiss
        ) { component ->
            SheetStackBottomSheetContent(component)
        }
    }
}

@Composable
private fun ScreenContent(component: SheetStackComponent) {
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
            val stack by component.bottomSheetPages.subscribeAsState()

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Stack:$stack"
            )

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Single Dialog",
                onClick = {
                    component.onOpenSingleDialogClick()
                }
            )

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Few Dialogs",
                onClick = {
                    component.onOpenFewDialogsClick()
                }
            )

            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Many Dialogs",
                onClick = {
                    component.onOpenManyDialogsClick()
                }
            )

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

@Composable
private fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onClick
    ) {
        Text(
            text = text
        )
    }
}

@Preview(device = "id:pixel_9")
@Composable
private fun HabitatListScreenPreview() {
    MarsColonyTheme {
        SheetStackScreen(PreviewSheetStackComponent())
    }
}
