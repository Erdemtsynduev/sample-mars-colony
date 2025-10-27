package com.alaershov.mars_colony.demo_dialog

import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentState

data class DemoDialogState(
    val message: String,
    val button: String,
) : BottomSheetContentState {

    override val isDismissAllowed: Boolean = true
}
