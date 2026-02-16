package com.alaershov.mars_colony.habitat.list_screen.dialog_pages

import com.arkivanov.decompose.router.pages.PagesNavigator

/**
 * Pushes a new dialog config onto the stack and selects it.
 */
fun <C : Any> PagesNavigator<C>.pushNew(
    configuration: C,
    onComplete: (isSuccess: Boolean) -> Unit = {},
) {
    navigate(
        transformer = { pages ->
            val newItems = if (pages.items.lastOrNull() == configuration) {
                pages.items
            } else {
                pages.items + configuration
            }
            pages.copy(
                items = newItems,
                selectedIndex = newItems.size - 1,
            )
        },
        onComplete = { newPages, oldPages ->
            onComplete(newPages.items.size > oldPages.items.size)
        }
    )
}

/**
 * Pops the top dialog from the stack.
 */
fun <C : Any> PagesNavigator<C>.pop(onComplete: (isSuccess: Boolean) -> Unit = {}) {
    navigate(
        transformer = { pages ->
            val newItems = pages.items
                .takeIf { it.isNotEmpty() }
                ?.dropLast(1)
                ?: pages.items
            pages.copy(
                items = newItems,
                selectedIndex = (newItems.size - 1).coerceAtLeast(0),
            )
        },
        onComplete = { newPages, oldPages ->
            onComplete(newPages.items.size < oldPages.items.size)
        }
    )
}
