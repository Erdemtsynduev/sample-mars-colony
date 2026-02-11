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
    @Assisted("onCloseFirstClick")
    private val onCloseFirstClick: () -> Unit,
    @Assisted("onCloseSecondClick")
    private val onCloseSecondClick: () -> Unit,
    @Assisted("onCloseHalfClick")
    private val onCloseHalfClick: () -> Unit,
    @Assisted("onReplaceClick")
    private val onReplaceClick: () -> Unit,
    @Assisted("onAddClick")
    private val onAddClick: () -> Unit,
    @Assisted("onAddFirstClick")
    private val onAddFirstClick: () -> Unit,
    @Assisted("onAddMiddleClick")
    private val onAddMiddleClick: () -> Unit,
    @Assisted("onShuffleClick")
    private val onShuffleClick: () -> Unit,
    @Assisted("onShiftForwardClick")
    private val onShiftForwardClick: () -> Unit,
    @Assisted("onShiftBackwardClick")
    private val onShiftBackwardClick: () -> Unit,
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

    override fun onCloseFirstClick() {
        onCloseFirstClick.invoke()
    }

    override fun onCloseSecondClick() {
        onCloseSecondClick.invoke()
    }

    override fun onCloseHalfClick() {
        onCloseHalfClick.invoke()
    }

    override fun onReplaceClick() {
        onReplaceClick.invoke()
    }

    override fun onAddClick() {
        onAddClick.invoke()
    }

    override fun onAddFirstClick() {
        onAddFirstClick.invoke()
    }

    override fun onAddMiddleClick() {
        onAddMiddleClick.invoke()
    }

    override fun onShuffleClick() {
        onShuffleClick.invoke()
    }

    override fun onShiftForwardClick() {
        onShiftForwardClick.invoke()
    }

    override fun onShiftBackwardClick() {
        onShiftBackwardClick.invoke()
    }

    override fun toString(): String {
        return "DefaultDemoDialogComponent@${hashCode().toHexString()} size=${state.value.size}"
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
            @Assisted("onCloseFirstClick")
            onCloseFirstClick: () -> Unit,
            @Assisted("onCloseSecondClick")
            onCloseSecondClick: () -> Unit,
            @Assisted("onCloseHalfClick")
            onCloseHalfClick: () -> Unit,
            @Assisted("onReplaceClick")
            onReplaceClick: () -> Unit,
            @Assisted("onAddClick")
            onAddClick: () -> Unit,
            @Assisted("onAddFirstClick")
            onAddFirstClick: () -> Unit,
            @Assisted("onAddMiddleClick")
            onAddMiddleClick: () -> Unit,
            @Assisted("onShuffleClick")
            onShuffleClick: () -> Unit,
            @Assisted("onShiftForwardClick")
            onShiftForwardClick: () -> Unit,
            @Assisted("onShiftBackwardClick")
            onShiftBackwardClick: () -> Unit,
        ): DefaultDemoDialogComponent
    }
}
