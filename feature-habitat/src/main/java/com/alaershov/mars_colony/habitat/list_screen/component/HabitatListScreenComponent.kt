package com.alaershov.mars_colony.habitat.list_screen.component

import com.alaershov.mars_colony.habitat.list_screen.DialogChild
import com.alaershov.mars_colony.habitat.list_screen.HabitatListScreenState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow

interface HabitatListScreenComponent {

    val state: StateFlow<HabitatListScreenState>

    val dialogSlot: Value<ChildSlot<*, DialogChild>>

    fun onBackClick()

    fun onBuildClick()

    fun onHabitatClick(id: String)

    fun onDialogDismiss()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
            onBackClick: () -> Unit,
        ): HabitatListScreenComponent
    }
}
