package com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation

import com.arkivanov.decompose.router.pages.PagesNavigator
import kotlin.random.Random

/**
 * Аналог `StackNavigator<C>.pushNew()` для PagesNavigator.
 *
 * Добавляет переданную [configuration] как последнюю страницу Pages, и делает её выбранной.
 *
 * Ничего не делает, если [configuration] уже является последней страницей.
 *
 * Decompose выкинет исключение, если [configuration] уже есть в стеке (кроме последней страницы).
 *
 * @param onComplete вызывается, когда навигация завершена (синхронно или асинхронно).
 * `isSuccess` будет `true`, если страница добавилась в стек.
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
 * Аналог `StackNavigator<C>.pop()` для PagesNavigator.
 *
 * Убирает последнюю страницу из стека.
 *
 * @param onComplete вызывается, когда навигация завершена (синхронно или асинхронно).
 * `isSuccess` будет `true`, если в стеке была хотя бы одна страница,
 * и она была убрана в результате работы этого метода
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
                selectedIndex = newItems.lastIndex,
            )
        },
        onComplete = { newPages, oldPages ->
            onComplete(newPages.items.size < oldPages.items.size)
        }
    )
}

/**
 * Pop random configuration from the stack.
 * Components that remain in the stack are not recreated, components that are no longer in the stack are destroyed.
 *
 * @param onComplete called when the navigation is finished (either synchronously or asynchronously).
 */
inline fun <C : Any> PagesNavigator<C>.popRandom(crossinline onComplete: () -> Unit = { }) {
    navigate(transformer = { pages ->
        if (pages.items.isEmpty()) {
            pages
        } else {
            val randomIndex = Random.Default.nextInt(pages.items.size)
            val newItems = pages.items.filterIndexed { index, item -> index != randomIndex }
            pages.copy(
                items = newItems,
                selectedIndex = newItems.lastIndex,
            )
        }
    }, onComplete = { _, _ -> onComplete() })
}

/**
 * Replaces all configurations currently in the stack with the provided [configurations].
 * Components that remain in the stack are not recreated, components that are no longer in the stack are destroyed.
 *
 * @param onComplete called when the navigation is finished (either synchronously or asynchronously).
 */
inline fun <C : Any> PagesNavigator<C>.replaceAll(vararg configurations: C, crossinline onComplete: () -> Unit = { }) {
    navigate(transformer = { pages ->
        val newItems = configurations.toList()
        pages.copy(
            items = newItems,
            selectedIndex = newItems.lastIndex,
        )
    }, onComplete = { _, _ -> onComplete() })
}
