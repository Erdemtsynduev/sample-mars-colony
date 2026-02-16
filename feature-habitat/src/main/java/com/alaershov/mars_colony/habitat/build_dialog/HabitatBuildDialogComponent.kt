package com.alaershov.mars_colony.habitat.build_dialog

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

interface HabitatBuildDialogComponent {

    val state: StateFlow<HabitatBuildDialogState>

    fun onPlusClick()

    fun onMinusClick()

    fun onBuildClick()

    fun onDismiss()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
            onDismiss: () -> Unit,
        ): HabitatBuildDialogComponent
    }
}
