package com.alaershov.mars_colony.sheet_stack.component

import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value

interface SheetStackComponent {

    val bottomSheetPages: Value<ChildPages<*, BottomSheetContentComponent>>

    fun onBottomSheetPagesDismiss()

    fun onOpenSingleDialogClick()

    fun onOpenFewDialogsClick()

    fun onOpenManyDialogsClick()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
        ): SheetStackComponent
    }
}
