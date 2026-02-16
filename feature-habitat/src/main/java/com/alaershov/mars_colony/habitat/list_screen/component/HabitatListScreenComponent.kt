package com.alaershov.mars_colony.habitat.list_screen.component

import com.alaershov.mars_colony.habitat.list_screen.DialogChild
import com.alaershov.mars_colony.habitat.list_screen.HabitatListScreenState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.StateFlow

interface HabitatListScreenComponent {

    val state: StateFlow<HabitatListScreenState>

    /** Stack of dialogs: items are bottom-to-top, selectedIndex is the top. */
    val dialogPages: Value<ChildPages<*, DialogChild>>

    fun onBackClick()

    fun onBuildClick()

    fun onHabitatClick(id: String)

    /** Pops the top dialog from the stack. */
    fun onDialogDismiss()

    interface Factory {

        fun create(
            componentContext: ComponentContext,
            onBackClick: () -> Unit,
        ): HabitatListScreenComponent
    }
}
