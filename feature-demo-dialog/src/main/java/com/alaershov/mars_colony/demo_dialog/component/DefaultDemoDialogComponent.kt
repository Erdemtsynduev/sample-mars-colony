package com.alaershov.mars_colony.demo_dialog.component

import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow

class DefaultDemoDialogComponent @AssistedInject internal constructor(
    @Assisted
    componentContext: ComponentContext,
    @Assisted
    dialogState: DemoDialogState,
    @Assisted
    private val onButtonClick: () -> Unit,
) : DemoDialogComponent, ComponentContext by componentContext {

    override val state = MutableStateFlow(dialogState)

    override val bottomSheetContentState = state

    override fun onButtonClick() {
        onButtonClick.invoke()
    }

    @AssistedFactory
    interface Factory : DemoDialogComponent.Factory {

        override fun create(
            componentContext: ComponentContext,
            dialogState: DemoDialogState,
            onButtonClick: () -> Unit,
        ): DefaultDemoDialogComponent
    }
}
