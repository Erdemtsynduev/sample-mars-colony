package com.alaershov.mars_colony.bottom_sheet.unstyled.non_modal

import android.util.Log
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.pages.ChildPages

/**
 * Вычисление нового UI-состояния стека на основе старого UI-состояния и нового логического состояния.
 *
 * В простой версии этот дифф помечает исчезнувшие в логическом состоянии элементы для анимированного удаления на UI.
 * Если изменений было слишком много, перестраивает весь список с нуля, без анимированных удалений.
 *
 * TODO алгоритм Myers Diff может помочь анимировать более сложные изменения списка
 */
fun List<BottomSheetItem<BottomSheetContentComponent>>.diffNonModal(
    newStack: ChildPages<*, BottomSheetContentComponent>,
): List<BottomSheetItem<BottomSheetContentComponent>> {
    val oldComponents = this.map { it.instance }
    val newComponents = newStack.items.map { it.instance }

    val removedComponents = oldComponents.filterNot { it in newComponents }

    return if (oldComponents.filterNot { it in removedComponents } == newComponents) {
        Log.d("UnstyledChildPagesMBS", "diff: remove with animation")
        // были только удаления, можно применить анимацию удаления
        this.map { item ->
            if (item.instance in removedComponents) {
                item.copy(isDismissedFromNavigation = true)
            } else {
                item
            }
        }
    } else {
        if (isLastComponentReplaced(oldComponents, newComponents)) {
            // со списком произошло что-то сложное, пока не умеем обрабатывать, делаем новый стек
            Log.d("UnstyledChildPagesMBS", "diff: recreate, need Myers diff")
            val newLastChild = newStack.toList().last()

            this.mapIndexed { index, item ->
                if (index == this.lastIndex) {
                    item.copy(isDismissedFromNavigation = true)
                } else {
                    item
                }
            } + newLastChild.toItem()
        } else {
            // со списком произошло что-то сложное, пока не умеем обрабатывать, делаем новый стек
            Log.d("UnstyledChildPagesMBS", "diff: recreate, need Myers diff")
            val newList: List<Child.Created<Any, BottomSheetContentComponent>> = newStack.toList()

            newList.map { (configuration, instance) ->
                BottomSheetItem(
                    configuration = configuration,
                    instance = instance,
                    isDismissedFromNavigation = false,
                )
            }
        }
    }
}

private fun Child.Created<Any, BottomSheetContentComponent>.toItem(): BottomSheetItem<BottomSheetContentComponent> =
    BottomSheetItem(
        configuration = configuration,
        instance = instance,
        isDismissedFromNavigation = false,
    )

private fun ChildPages<*, BottomSheetContentComponent>.toList(): List<Child.Created<Any, BottomSheetContentComponent>> {
    return items.mapNotNull { it as? Child.Created }
}

private fun isLastComponentReplaced(
    oldComponents: List<BottomSheetContentComponent>,
    newComponents: List<BottomSheetContentComponent?>
): Boolean {
    if (oldComponents.isEmpty() || newComponents.isEmpty()) {
        return false
    }
    return oldComponents.dropLast(1) == newComponents.dropLast(1) &&
            oldComponents.last() != newComponents.last()
}
