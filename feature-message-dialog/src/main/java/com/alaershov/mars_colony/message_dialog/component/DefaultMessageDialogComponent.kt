package com.alaershov.mars_colony.message_dialog.component

import com.alaershov.mars_colony.message_dialog.MessageDialogState
import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow

class DefaultMessageDialogComponent @AssistedInject internal constructor(
    @Assisted
    componentContext: ComponentContext,
    @Assisted
    dialogState: MessageDialogState,
    @Assisted
    private val onButtonClick: () -> Unit,
    @Assisted("onDismiss")
    private val onDismiss: () -> Unit,
) : MessageDialogComponent, ComponentContext by componentContext {

    override val state = MutableStateFlow(dialogState)

    override fun onButtonClick() {
        onButtonClick.invoke()
    }

    override fun onDismiss() = onDismiss.invoke()

    @AssistedFactory
    interface Factory : MessageDialogComponent.Factory {

        override fun create(
            componentContext: ComponentContext,
            dialogState: MessageDialogState,
            onButtonClick: () -> Unit,
            @Assisted("onDismiss")
            onDismiss: () -> Unit,
        ): DefaultMessageDialogComponent
    }
}
