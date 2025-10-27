package com.alaershov.mars_colony.sheet_stack.bottom_sheet

import androidx.compose.runtime.Composable
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.demo_dialog.DemoDialog
import com.alaershov.mars_colony.demo_dialog.component.DemoDialogComponent

@Composable
fun SheetStackBottomSheetContent(component: BottomSheetContentComponent) {
    when (component) {
        is DemoDialogComponent -> {
            DemoDialog(component)
        }
    }
}
