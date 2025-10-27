package com.alaershov.mars_colony.sheet_stack.component

import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.sheet_stack.bottom_sheet.SheetStackBottomSheetConfig
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.MutableValue

class PreviewSheetStackComponent : SheetStackComponent {

    override val bottomSheetPages = MutableValue<ChildPages<SheetStackBottomSheetConfig, BottomSheetContentComponent>>(
        ChildPages(listOf(), 0)
    )

    override fun onBottomSheetPagesDismiss() {}

    override fun onOpenSingleDialogClick() {}

    override fun onOpenFewDialogsClick() {}

    override fun onOpenManyDialogsClick() {}
}
