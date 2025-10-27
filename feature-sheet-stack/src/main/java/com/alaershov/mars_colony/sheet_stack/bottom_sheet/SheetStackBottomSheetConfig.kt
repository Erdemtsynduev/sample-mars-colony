package com.alaershov.mars_colony.sheet_stack.bottom_sheet

import kotlinx.serialization.Serializable

@Serializable
sealed class SheetStackBottomSheetConfig {

    @Serializable
    data class Sheet(
        val list: List<String>
    ) : SheetStackBottomSheetConfig()
}
