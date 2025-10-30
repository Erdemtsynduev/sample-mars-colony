@file:OptIn(ExperimentalMaterial3Api::class)

package com.alaershov.mars_colony.sheet_stack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.bottom_sheet.material3.pages.ChildPagesModalBottomSheet
import com.alaershov.mars_colony.bottom_sheet.unstyled.non_modal.UnstyledChildPagesBottomSheet
import com.alaershov.mars_colony.bottom_sheet.unstyled.modal.UnstyledChildPagesModalBottomSheet
import com.alaershov.mars_colony.sheet_stack.bottom_sheet.SheetStackBottomSheetContent
import com.alaershov.mars_colony.sheet_stack.component.PreviewSheetStackComponent
import com.alaershov.mars_colony.sheet_stack.component.SheetStackComponent
import com.alaershov.mars_colony.sheet_stack.component.SheetStackMode
import com.alaershov.mars_colony.ui.R
import com.alaershov.mars_colony.ui.theme.MarsColonyTheme
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages

@Composable
fun SheetStackScreen(component: SheetStackComponent) {
    Box {
        ScreenContent(component)

        BottomSheetStackText(component)

        val state by component.state.collectAsState()

        when (state.mode) {
            SheetStackMode.MATERIAL_3_MODAL -> {
                ChildPagesModalBottomSheet(
                    sheetContentPagesState = component.bottomSheetPages,
                    onDismiss = component::onBottomSheetPagesDismiss,
                ) { component ->
                    SheetStackBottomSheetContent(component)
                }
            }

            SheetStackMode.UNSTYLED_MODAL -> {
                UnstyledChildPagesModalBottomSheet(
                    sheetContentPagesState = component.bottomSheetPages,
                    onDismiss = component::onBottomSheetPagesDismiss,
                ) { component ->
                    SheetStackBottomSheetContent(component)
                }
            }

            SheetStackMode.UNSTYLED_NON_MODAL -> {
                UnstyledChildPagesBottomSheet(
                    sheetContentPagesState = component.bottomSheetPages,
                    onDismiss = component::onBottomSheetPagesDismiss,
                ) { component ->
                    SheetStackBottomSheetContent(component)
                }
            }
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
                        component.onBackClick()
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

@Composable
private fun BottomSheetStackText(component: SheetStackComponent) {
    val stack by component.bottomSheetPages.subscribeAsState()

    Box(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.5f))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            text = "Stack:${stack.toPrettyString()}",
            color = Color.White,
        )
    }
}

private fun ChildPages<*, BottomSheetContentComponent>.toPrettyString(): String {
    return buildString {
        appendLine("Selected Index = $selectedIndex")
        items.forEachIndexed { index, child ->
            appendLine("$index: config=${child.configuration} instance=${child.instance.hashCode().toHexString()}")
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
