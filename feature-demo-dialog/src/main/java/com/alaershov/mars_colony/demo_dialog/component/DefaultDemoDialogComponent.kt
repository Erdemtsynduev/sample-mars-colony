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
    @Assisted("onButtonClick")
    private val onButtonClick: () -> Unit,
    @Assisted("onCloseClick")
    private val onCloseClick: () -> Unit,
    @Assisted("onCloseAllClick")
    private val onCloseAllClick: () -> Unit,
    @Assisted("onCloseRandomClick")
    private val onCloseRandomClick: () -> Unit,
) : DemoDialogComponent, ComponentContext by componentContext {

    override val state = MutableStateFlow(dialogState)

    override val bottomSheetContentState = state

    override fun onButtonClick() {
        onButtonClick.invoke()
    }

    override fun onCloseClick() {
        onCloseClick.invoke()
    }

    override fun onCloseAllClick() {
        onCloseAllClick.invoke()
    }

    override fun onCloseRandomClick() {
        onCloseRandomClick.invoke()
    }

    @AssistedFactory
    interface Factory : DemoDialogComponent.Factory {

        override fun create(
            componentContext: ComponentContext,
            dialogState: DemoDialogState,
            @Assisted("onButtonClick")
            onButtonClick: () -> Unit,
            @Assisted("onCloseClick")
            onCloseClick: () -> Unit,
            @Assisted("onCloseAllClick")
            onCloseAllClick: () -> Unit,
            @Assisted("onCloseRandomClick")
            onCloseRandomClick: () -> Unit,
        ): DefaultDemoDialogComponent
    }
}
