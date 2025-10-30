package com.alaershov.mars_colony.bottom_sheet.unstyled.modal

data class ModalBottomSheetItem<out T : Any>(
    val configuration: Any,
    val instance: T,
    val isDismissedFromNavigation: Boolean,
)
