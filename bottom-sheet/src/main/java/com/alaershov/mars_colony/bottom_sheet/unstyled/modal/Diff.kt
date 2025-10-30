package com.alaershov.mars_colony.bottom_sheet.unstyled.modal

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
fun List<ModalBottomSheetItem<BottomSheetContentComponent>>.diffModal(
    newStack: ChildPages<*, BottomSheetContentComponent>,
): List<ModalBottomSheetItem<BottomSheetContentComponent>> {
    val oldConfigs = this.map { it.configuration }
    val newConfigs = newStack.items.map { it.configuration }

    val removedConfigs = oldConfigs.filterNot { it in newConfigs }

    return if (oldConfigs.filterNot { it in removedConfigs } == newConfigs) {
        Log.d("UnstyledChildPagesMBS", "diff: remove with animation")
        // были только удаления, можно применить анимацию удаления
        this.map { item ->
            if (item.configuration in removedConfigs) {
                item.copy(isDismissedFromNavigation = true)
            } else {
                item
            }
        }
    } else {
        // со списком произошло что-то сложное, пока не умеем обрабатывать, делаем новый стек
        Log.d("UnstyledChildPagesMBS", "diff: recreate, need Myers diff")
        val newList: List<Child.Created<Any, BottomSheetContentComponent>> = newStack.items
            .mapNotNull { it as? Child.Created }
        return newList.map { (configuration, instance) ->
            ModalBottomSheetItem(
                configuration = configuration,
                instance = instance,
                isDismissedFromNavigation = false,
            )
        }
    }
}
