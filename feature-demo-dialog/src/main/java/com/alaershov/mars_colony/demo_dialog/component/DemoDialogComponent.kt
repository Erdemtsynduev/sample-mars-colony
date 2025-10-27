package com.alaershov.mars_colony.demo_dialog.component

import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface DemoDialogComponent : BottomSheetContentComponent {

    val state: StateFlow<DemoDialogState>

    fun onButtonClick()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
            dialogState: DemoDialogState,
            onButtonClick: () -> Unit,
        ): DemoDialogComponent
    }
}
