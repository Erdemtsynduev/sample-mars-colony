package com.alaershov.mars_colony.sheet_stack.bottom_sheet

import androidx.compose.runtime.Composable
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.message_dialog.MessageDialog
import com.alaershov.mars_colony.message_dialog.component.MessageDialogComponent

@Composable
fun SheetStackBottomSheetContent(component: BottomSheetContentComponent) {
    when (component) {
        is MessageDialogComponent -> {
            MessageDialog(component)
        }
    }
}
