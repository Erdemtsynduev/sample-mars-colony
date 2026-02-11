package com.alaershov.mars_colony.bottom_sheet.unstyled.non_modal

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.arkivanov.decompose.router.pages.ChildPages

@Stable
class BottomSheetItemStackState(
    initialPages: ChildPages<*, BottomSheetContentComponent>,
    private val onDismissItem: (BottomSheetItem<BottomSheetContentComponent>) -> Unit,
) {
    private val _stack: MutableState<List<BottomSheetItem<BottomSheetContentComponent>>> = mutableStateOf(
        emptyList<BottomSheetItem<BottomSheetContentComponent>>().diffNonModal(
            newStack = initialPages,
        )
    )
    val stack: State<List<BottomSheetItem<BottomSheetContentComponent>>> = _stack

    fun update(pages: ChildPages<*, BottomSheetContentComponent>) {
        val oldStack = _stack.value

        Log.d(
            "BottomSheetItemStackState",
            "update diff: \nold=$oldStack\nnew=${pages.items.map { it.configuration }}\n"
        )

        val newStack = oldStack.diffNonModal(pages)
        _stack.value = newStack

        Log.d(
            "BottomSheetItemStackState",
            "diff result: ${newStack.map { "${it.configuration} dismissed=${it.isDismissedFromNavigation}" }}"
        )
    }

    fun onHidden(instance: BottomSheetContentComponent) {
        Log.d(
            "BottomSheetItemStackState",
            "onHidden: $instance"
        )

        val item = findItemByComponentOrNull(instance)

        if (item == null) {
            Log.d(
                "BottomSheetItemStackState",
                "onHidden: item is null $instance"
            )
            return
        }

        val oldStack = _stack.value
        val newStack = oldStack.filterNot { it.instance == item.instance }
        _stack.value = newStack

        // TODO возможно больше условий
        if (!item.isDismissedFromNavigation) {
            Log.d(
                "BottomSheetItemStackState",
                "onHidden: dismiss $item"
            )
            onDismissItem.invoke(item)
        } else {
            Log.d(
                "BottomSheetItemStackState",
                "onHidden: already dismissed from navigation $item"
            )
        }
    }

    fun onDismiss(instance: BottomSheetContentComponent) {
        val item = findItemByComponentOrNull(instance)
        if (item != null) {

            if (item.isDismissedFromNavigation) {
                Log.d(
                    "BottomSheetItemStackState",
                    "onDismiss: skip (already being removed) $item"
                )
            } else {
                Log.d(
                    "BottomSheetItemStackState",
                    "onDismiss: call onDismissItem $item"
                )
                // TODO может сразу пометить item как закрывающийся?
                onDismissItem.invoke(item)
            }

        } else {
            Log.d(
                "BottomSheetItemStackState",
                "onDismiss: no item found for $instance"
            )
        }
    }

    private fun findItemByComponentOrNull(
        component: BottomSheetContentComponent
    ): BottomSheetItem<BottomSheetContentComponent>? {
        return _stack.value.find { it.instance == component }
    }
}
