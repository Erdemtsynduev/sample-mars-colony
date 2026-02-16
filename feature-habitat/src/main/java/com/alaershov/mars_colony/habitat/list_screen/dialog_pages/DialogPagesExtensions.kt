package com.alaershov.mars_colony.habitat.list_screen.dialog_pages

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.KSerializer

/**
 * Page status for dialog stack: all pages are kept (CREATED or RESUMED)
 * so that multiple ModalBottomSheets can be displayed in stack.
 */
fun getDialogPageStatus(index: Int, pages: Pages<*>): ChildNavState.Status {
    return if (index == pages.selectedIndex) {
        ChildNavState.Status.RESUMED
    } else {
        ChildNavState.Status.CREATED
    }
}

/**
 * Creates ChildPages for a stack of dialogs (all pages kept alive for stacking).
 */
inline fun <reified C : Any, T : Any> ComponentContext.dialogPages(
    source: PagesNavigation<C>,
    serializer: KSerializer<C>?,
    noinline initialPages: () -> Pages<C> = { Pages() },
    key: String = "HabitatDialogPages",
    noinline pageStatus: (index: Int, Pages<C>) -> ChildNavState.Status = ::getDialogPageStatus,
    handleBackButton: Boolean = true,
    noinline childFactory: (configuration: C, ComponentContext) -> T,
): Value<ChildPages<C, T>> =
    childPages(
        source = source,
        serializer = serializer,
        initialPages = initialPages,
        key = key,
        pageStatus = pageStatus,
        handleBackButton = handleBackButton,
        childFactory = childFactory,
    )
