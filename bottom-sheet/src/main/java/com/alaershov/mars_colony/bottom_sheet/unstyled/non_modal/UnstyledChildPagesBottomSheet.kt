@file:OptIn(ExperimentalMaterial3Api::class)

package com.alaershov.mars_colony.bottom_sheet.unstyled.non_modal

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import com.composables.core.BottomSheet
import com.composables.core.BottomSheetState
import com.composables.core.DragIndication
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberBottomSheetState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach

// TODO поддержать back press со стороны декомпоза
@Composable
fun UnstyledChildPagesBottomSheet(
    sheetContentPagesState: Value<ChildPages<*, BottomSheetContentComponent>>,
    onDismiss: (Any) -> Unit,
    modifier: Modifier = Modifier,
    key: String = "UnstyledChildPagesMBS",
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable (BoxScope.(BottomSheetContentComponent) -> Unit),
) {
    // TODO обобщить логику стека для разных UI реализаций
    // наблюдаем за состоянияем ChildPages
    // при каждом изменении стека навигации страниц - добавлении или удалении - значение обновится.
    val sheetContentStack: ChildPages<*, BottomSheetContentComponent> by sheetContentPagesState.subscribeAsState()

    val currentOnDismiss = rememberUpdatedState(onDismiss)

    val bottomSheetItemStackState = remember {
        BottomSheetItemStackState(
            initialPages = sheetContentStack,
            onDismissItem = { item ->
                currentOnDismiss.value.invoke(item.configuration)
            }
        )
    }

    // реальное состояние стека БШ, отображаемое на экране. Будет отличаться от состояния навигации,
    // например, если в навигации удалился БШ, то тут он должен анимированно закрыться, и только потом удалиться
    val stack: List<BottomSheetItem<BottomSheetContentComponent>> by bottomSheetItemStackState.stack

    DisposableEffect(sheetContentStack) {
        // применяем изменения в логическом стеке навигации к состоянию UI
        bottomSheetItemStackState.update(sheetContentStack)

        onDispose {}
    }

    DisposableEffect(stack) {
        Log.d(key, "stack ${stack.map { it.configuration }}")
        onDispose {}
    }

    for (item in stack) {
        key(item.instance) {
            val bottomSheetState = rememberBottomSheetState(
                initialDetent = Hidden,
                animationSpec = tween(2000),
            )

            LaunchedEffect(bottomSheetState) {
                Log.d(
                    key,
                    "modalSheetState@${bottomSheetState.hashCode().toHexString()} observe"
                )
                snapshotFlow {
                    Triple(bottomSheetState.isIdle, bottomSheetState.currentDetent, bottomSheetState.targetDetent)
                }
                    .drop(1) // первая композиция в полностью скрытом состоянии
                    .onEach { (isIdle, currentDetent, targetDetent) ->
//                    Log.d(
//                        key,
//                        "modalSheetState${
//                            modalSheetState.hashCode().toHexString()
//                        } change: isIdle=$isIdle currentDetent=${currentDetent.identifier}, targetDetent=${targetDetent.identifier}"
//                    )
                        if (isIdle && currentDetent == Hidden) {
                            bottomSheetItemStackState.onHidden(item.instance)
                        }
                    }.collect()
            }

            // чтобы БШ открылся с анимацией, сначала он Hidden, и сразу запускаем анимацию открытия
            LaunchedEffect(bottomSheetState) {
                if (bottomSheetState.targetDetent != FullyExpanded) {
                    Log.d(key, "expand ${item.configuration} state=$bottomSheetState")
                    bottomSheetState.targetDetent = FullyExpanded
                }
            }

            LaunchedEffect(bottomSheetState, item.isDismissedFromNavigation) {
                try {
                    if (item.isDismissedFromNavigation) {
                        Log.d(key, "hide ${item.configuration} state=$bottomSheetState")
                        bottomSheetState.animateTo(Hidden)
                        Log.d(key, "hidden ${item.configuration} state=$bottomSheetState")
                    }
                } catch (e: Exception) {
                    // TODO если во время анимации сворачивания поймать пальцем БШ,
                    //  тут будет MutationInterruptedException: Mutation interrupted
                    //  В этом случае в навигации уже не будет компонента, а БШ останется на экране
                    //  Надо перехватить жесты и дать БШ спокойно закрыться
                    //  и кнопку back запретить повторно нажимать, она рестартует анимацию
                    Log.d(key, "hide cancelled! ${item.configuration} state=$bottomSheetState", e)
                }
            }

            ModalBottomSheetImpl(
                bottomSheetState = bottomSheetState,
                item = item,
                onDismiss = {
                    // Этот коллбек стреляет в трёх случаях
                    // - когда нажимаешь Back
                    // - когда тапаешь на Scrim
                    // - когда анимация сворачивания доиграла, и диалог полностью свернулся
                    //
                    // Это означает, что он может сработать дважды, например:
                    // 1) нажали Back, диалог начал анимированно сворачиваться
                    // 2) диалог свернулся, анимация закончилась
                    bottomSheetItemStackState.onDismiss(item.instance)
                },
                content = content
            )
        }

    }
}

@Composable
private fun ModalBottomSheetImpl(
    bottomSheetState: BottomSheetState,
    item: BottomSheetItem<BottomSheetContentComponent>,
    onDismiss: () -> Unit,
    content: @Composable (BoxScope.(BottomSheetContentComponent) -> Unit)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.3f))
            .let {
                it.pointerInput(Unit) {
                    detectTapGestures {
                        onDismiss()
                    }
                }
            }
    ) {
        BottomSheet(
            state = bottomSheetState,
            backgroundColor = Color.White,
            contentColor = Color.Black,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .widthIn(max = 640.dp)
                .fillMaxWidth(),
        ) {
            val scope = this
            DisposableEffect(bottomSheetState) {
                Log.d("UnstyledChildPagesMBS", "scope=$scope state=$bottomSheetState")
                onDispose { }
            }

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

                content(item.instance)
            }
        }
    }
}
