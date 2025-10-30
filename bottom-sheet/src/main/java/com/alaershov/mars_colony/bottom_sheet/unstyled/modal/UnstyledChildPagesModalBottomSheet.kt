@file:OptIn(ExperimentalMaterial3Api::class)

package com.alaershov.mars_colony.bottom_sheet.unstyled.modal

import android.util.Log
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.composables.core.ModalBottomSheetState
import com.composables.core.ModalSheetProperties
import com.composables.core.Scrim
import com.composables.core.Sheet
import com.composables.core.SheetDetent.Companion.FullyExpanded
import com.composables.core.SheetDetent.Companion.Hidden
import com.composables.core.rememberModalBottomSheetState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach

/**
 * Unstyled ModalBottomSheet не поддерживает операции со стеком диалогов,
 * кроме открытия и закрытия диалога на верхушке стека.
 *
 * Причины две:
 * 1) Состояние ModalBottomSheetState обязательно нужно ремемберить в том же скоупе композиции, в котором
 * будет вызван ModalBottomSheet с этим состоянием. ModalBottomSheet внутри себя снова делает
 * `remember { Scope(state) }`, и после первой композиции ему уже нельзя подставить другой state,
 * он будет его игнорировать. То есть, мы не можем привязать состояние ModalBottomSheet к логическому Item-у в стеке,
 * оно обязано быть привязано к композиции. https://t.me/arkivanov_kt/31872
 *
 * 2) Поряок отрисовки БШ зависит не от порядка их расположения в стеке, а от ВРЕМЕНИ появления БШ на экране.
 * Внутри ModalBottomSheet есть проверка `if (visible) { Modal(...) }`, а Modal это Dialog,
 * который открывает Window, которое добавляется в общий список окон просто поверх тех, что уже открыты.
 *
 * Пример: если в стеке есть [A, B], я закрою композный ModalBottomSheet для "А", а потом его заново открою,
 * не убирая из композиции, то он откроется ПОВЕРХ "B".
 */
@Composable
fun UnstyledChildPagesModalBottomSheet(
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
    // наблюдаем за состоянияем ChildPages
    // при каждом изменении стека навигации страниц - добавлении или удалении - значение обновится.
    val sheetContentStack: ChildPages<*, BottomSheetContentComponent> by sheetContentPagesState.subscribeAsState()

    // Реальное состояние стека БШ, отображаемое на экране.
    // Может отличаться от состояния навигации, например, если в навигации удалился БШ,
    // то тут он должен сначала анимированно закрыться, и только потом удалиться.
    var realStack: List<ModalBottomSheetItem<BottomSheetContentComponent>> by remember {
        mutableStateOf(
            emptyList<ModalBottomSheetItem<BottomSheetContentComponent>>().diffModal(
                newStack = sheetContentStack,
            )
        )
    }

    DisposableEffect(sheetContentStack) {
        // применяем изменения в стеке навигации к состоянию UI стека
        Log.d(key, "diff: \nold=${realStack}\nnew=${sheetContentStack.items.map { it.configuration }}\n")
        val newStack = realStack.diffModal(
            newStack = sheetContentStack,
        )
        Log.d(key, "diff result: ${newStack.map { "${it.configuration} dismissed=${it.isDismissedFromNavigation}" }}")

        realStack = newStack

        onDispose {}
    }

    DisposableEffect(realStack) {
        Log.d(key, "realStack ${realStack.map { it.configuration }}")
        onDispose {}
    }

    for (item in realStack) {
        // если всё завернуть в key(item.configuration), то диалоги будут отображаться не по порядку списка

        // Состояние ModalBottomSheetState обязательно нужно ремемберить в том же скоупе композиции,
        // в котором будет вызван ModalBottomSheet с этим состоянием.
        // ModalBottomSheet внутри себя снова делает remember, и после первой композиции
        // ему уже нельзя подставить другой state, он будет его игнорировать.
        val modalSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
            initialDetent = Hidden,
            detents = listOf(Hidden, FullyExpanded),
            animationSpec = tween(3000),
            confirmDetentChange = { true },
        )

        LaunchedEffect(modalSheetState) {
            Log.d(
                key,
                "observe ${modalSheetState.toPrettyString()}"
            )
            snapshotFlow {
                Triple(modalSheetState.isIdle, modalSheetState.currentDetent, modalSheetState.targetDetent)
            }
                .drop(1) // первая композиция в полностью скрытом состоянии
                .onEach { (isIdle, currentDetent, targetDetent) ->
                    if (isIdle && currentDetent == Hidden) {
                        Log.d(
                            key,
                            "realStack remove ${item.configuration} because hidden"
                        )
                        realStack = realStack.filterNot { it.configuration == item.configuration }
                    }
                }.collect()
        }

        // чтобы БШ открылся с анимацией, сначала он Hidden, и сразу запускаем анимацию открытия
        LaunchedEffect(modalSheetState, item) {
            Log.d(key, "init effect ${item.configuration} state=${modalSheetState.toPrettyString()}")

            if (modalSheetState.targetDetent != FullyExpanded && !item.isDismissedFromNavigation) {
                Log.d(key, "expand ${item.configuration} state=${modalSheetState.toPrettyString()}")
                modalSheetState.animateTo(FullyExpanded)
                Log.d(key, "expand success ${item.configuration} state=${modalSheetState.toPrettyString()}")
            }
        }

        LaunchedEffect(modalSheetState, item.isDismissedFromNavigation) {
            try {
                if (item.isDismissedFromNavigation) {
                    Log.d(key, "hide ${item.configuration} state=${modalSheetState.toPrettyString()}")
                    modalSheetState.animateTo(Hidden)
                    Log.d(key, "hidden ${item.configuration} state=${modalSheetState.toPrettyString()}")
                }
            } catch (e: Exception) {
                // TODO если во время анимации сворачивания поймать пальцем БШ,
                //  тут будет MutationInterruptedException: Mutation interrupted
                //  В этом случае в навигации уже не будет компонента, а БШ останется на экране
                //  Надо перехватить жесты и дать БШ спокойно закрыться
                //  и кнопку back запретить повторно нажимать, она рестартует анимацию
                Log.d(key, "hide cancelled! ${item.configuration} state=${modalSheetState.toPrettyString()}", e)
            }
        }

        ModalBottomSheetImpl(
            modalSheetState = modalSheetState,
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
                if (!item.isDismissedFromNavigation) {
                    Log.d(key, "onDismiss ${item.configuration}")
                    onDismiss(item.configuration)
                } else {
                    Log.d(key, "onDismiss skip (already being dismissed) ${item.configuration}")
                }
            },
            content = content
        )
    }
}

private fun ModalBottomSheetState.toPrettyString(): String {
    return "ModalBottomSheetState@${hashCode().toHexString()}(isIdle=$isIdle, current=${currentDetent.identifier}, target=${targetDetent.identifier})"
}

@Composable
private fun ModalBottomSheetImpl(
    modalSheetState: ModalBottomSheetState,
    item: ModalBottomSheetItem<BottomSheetContentComponent>,
    onDismiss: () -> Unit,
    content: @Composable (BoxScope.(BottomSheetContentComponent) -> Unit)
) {
    ModalBottomSheet(
        state = modalSheetState,
        properties = ModalSheetProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismiss = onDismiss,
    ) {
        val scope = this
        DisposableEffect(modalSheetState) {
            Log.d("UnstyledChildPagesMBS", "scope=$scope state=$modalSheetState")
            onDispose { }
        }

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

                    content(item.instance)
                }
            }
        }
    }
}
