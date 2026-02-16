package com.alaershov.mars_colony.sheet_stack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow

interface SheetStackComponent {

    val state: StateFlow<SheetStackScreenState>

    /** Stack of dialogs: items are bottom-to-top, selectedIndex is the top. */
    val dialogPages: Value<ChildPages<*, DialogChild>>

    fun onBottomSheetPagesDismiss()

    fun onBottomSheetPagesDismiss(config: Any)

    fun onBackClick()

    fun onOpenSingleDialogClick()

    fun onOpenFewDialogsClick()

    fun onOpenManyDialogsClick()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
            mode: SheetStackMode,
            onBackClick: () -> Unit,
        ): SheetStackComponent
    }
}
