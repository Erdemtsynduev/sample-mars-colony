package com.alaershov.mars_colony.demo_dialog.component

import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface DemoDialogComponent {

    val state: StateFlow<DemoDialogState>

    fun onButtonClick()

    fun onCloseClick()

    fun onCloseAllClick()

    fun onCloseFirstClick()

    fun onCloseSecondClick()

    fun onCloseHalfClick()

    fun onReplaceClick()

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
            onCloseSecondClick: () -> Unit,
            onCloseHalfClick: () -> Unit,
            onReplaceClick: () -> Unit,
            onAddClick: () -> Unit,
            onAddFirstClick: () -> Unit,
            onAddMiddleClick: () -> Unit,
            onShuffleClick: () -> Unit,
            onShiftForwardClick: () -> Unit,
            onShiftBackwardClick: () -> Unit,
        ): DemoDialogComponent
    }
}
