package com.alaershov.mars_colony.message_dialog.component

import com.alaershov.mars_colony.message_dialog.MessageDialogState
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface MessageDialogComponent {

    val state: StateFlow<MessageDialogState>

    fun onButtonClick()

    fun onDismiss()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
            dialogState: MessageDialogState,
            onButtonClick: () -> Unit,
            onDismiss: () -> Unit,
        ): MessageDialogComponent
    }
}
