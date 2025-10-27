@file:OptIn(ExperimentalMaterial3Api::class)

package com.alaershov.mars_colony.bottom_sheet.unstyled

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalSheetProperties
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState

@Composable
fun UnstyledChildPagesModalBottomSheet(
    sheetContentPagesState: Value<ChildPages<*, BottomSheetContentComponent>>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    key: String = "UnstyledChildPagesMBS",
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = androidx.compose.material3.contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable (BoxScope.(BottomSheetContentComponent) -> Unit),
) {
    // наблюдаем за состоянияем ChildPages
    // при каждом изменении стека навигации страниц - добавлении или удалении - значение обновится.
    val sheetContentStack: ChildPages<*, BottomSheetContentComponent> by sheetContentPagesState.subscribeAsState()

    // TODO сделать тестовый экран для стека БШ
    //  !!! DraggableCards вдохновение !!!
    //  - вставка на верхушку стека
    //  - удаление с верхушки
    //  - вставка в начало или в середину
    //  - множественная вставка
    //  - множественное удаление
    //  - swap


    for (page in sheetContentStack.items) {
        val modalSheetState = rememberModalBottomSheetState(
            initialDetent = Hidden,
            detents = listOf(Hidden, FullyExpanded),
            confirmDetentChange = {
                true
            }
        )

        // чтобы БШ открылся с анимацией, сначала он Hidden, и сразу запускаем анимацию открытия
        LaunchedEffect(modalSheetState) {
            modalSheetState.targetDetent = FullyExpanded
        }

        ModalBottomSheet(
            state = modalSheetState,
            properties = ModalSheetProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismiss = {
                Log.d(key, "onDismiss")
            },
        ) {
            Scrim(
                scrimColor = Color.Black.copy(0.3f),
                enter = fadeIn(),
                exit = fadeOut()
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)
                    .displayCutoutPadding()
                    .statusBarsPadding()
                    .padding(
                        WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
                            .asPaddingValues()
                    )
            ) {
                Sheet(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .widthIn(max = 640.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        DragIndication(
                            modifier = Modifier
                                .padding(top = 22.dp)
                                .background(Color.Black.copy(0.4f), RoundedCornerShape(100))
                                .width(32.dp)
                                .height(4.dp)
                        )

                        page.instance?.let { content(it) }
                    }
                }
            }
        }
    }
}
