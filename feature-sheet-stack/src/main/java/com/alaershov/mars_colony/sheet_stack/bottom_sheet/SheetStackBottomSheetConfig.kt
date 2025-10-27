package com.alaershov.mars_colony.sheet_stack.bottom_sheet

import kotlinx.serialization.Serializable

@Serializable
sealed class SheetStackBottomSheetConfig {

    @Serializable
    data object Sheet : SheetStackBottomSheetConfig()
}
