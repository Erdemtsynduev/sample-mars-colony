package com.alaershov.mars_colony.demo_dialog.component

import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface DemoDialogComponent : BottomSheetContentComponent {

    val state: StateFlow<DemoDialogState>

    fun onButtonClick()

    fun onCloseClick()

    fun onCloseAllClick()

    fun onCloseFirstClick()

    fun onCloseMiddleClick()

    fun onCloseHalfClick()

    fun onAddClick()

    fun onAddFirstClick()

    fun onAddMiddleClick()

    fun onShuffleClick()

    fun onShiftForwardClick()

    fun onShiftBackwardClick()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
            dialogState: DemoDialogState,
            onButtonClick: () -> Unit,
            onCloseClick: () -> Unit,
            onCloseAllClick: () -> Unit,
            onCloseFirstClick: () -> Unit,
            onCloseMiddleClick: () -> Unit,
            onCloseHalfClick: () -> Unit,
            onAddClick: () -> Unit,
            onAddFirstClick: () -> Unit,
            onAddMiddleClick: () -> Unit,
            onShuffleClick: () -> Unit,
            onShiftForwardClick: () -> Unit,
            onShiftBackwardClick: () -> Unit,
        ): DemoDialogComponent
    }
}
