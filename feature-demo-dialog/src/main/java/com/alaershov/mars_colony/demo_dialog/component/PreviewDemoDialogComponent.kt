package com.alaershov.mars_colony.demo_dialog.component

import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import kotlinx.coroutines.flow.MutableStateFlow

class PreviewDemoDialogComponent : DemoDialogComponent {

    override val state = MutableStateFlow(
        DemoDialogState(
            size = 3,
            message = "Preview Message",
            button = "Click me!"
        )
    )

    override val bottomSheetContentState = state

    override fun onButtonClick() {}

    override fun onCloseClick() {}

    override fun onCloseAllClick() {}

    override fun onCloseFirstClick() {}

    override fun onCloseRandomClick() {}

    override fun onCloseHalfClick() {}

    override fun onAddClick() {}

    override fun onAddFirstClick() {}

    override fun onAddMiddleClick() {}

    override fun onShuffleClick() {}

    override fun onShiftForwardClick() {}

    override fun onShiftBackwardClick() {}
}
