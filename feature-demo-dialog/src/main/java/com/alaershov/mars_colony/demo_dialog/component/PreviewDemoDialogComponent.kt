package com.alaershov.mars_colony.demo_dialog.component

import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import kotlinx.coroutines.flow.MutableStateFlow

class PreviewDemoDialogComponent : DemoDialogComponent {

    override val state = MutableStateFlow(
        DemoDialogState(
            message = "Preview Message",
            button = "Click me!"
        )
    )

    override val bottomSheetContentState = state

    override fun onButtonClick() {}

    override fun onCloseClick() {}

    override fun onCloseAllClick() {}

    override fun onCloseRandomClick() {}
}
