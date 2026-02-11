package com.alaershov.mars_colony.bottom_sheet.unstyled.non_modal

data class BottomSheetItem<out T : Any>(
    val configuration: Any,
    val instance: T,
    val isDismissedFromNavigation: Boolean,
)
