package com.alaershov.mars_colony.habitat.list_screen.component

import com.alaershov.mars_colony.habitat.Habitat
import com.alaershov.mars_colony.habitat.list_screen.DialogChild
import com.alaershov.mars_colony.habitat.list_screen.HabitatDialogConfig
import com.alaershov.mars_colony.habitat.list_screen.HabitatListScreenState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PreviewHabitatListScreenComponent : HabitatListScreenComponent {

    override val state: StateFlow<HabitatListScreenState> = MutableStateFlow(
        HabitatListScreenState(
            listOf(
                Habitat(
                    id = "1111-1111-1111-1111",
                    capacity = 40
                ),
                Habitat(
                    id = "2222-2222-2222-2222",
                    capacity = 20
                )
            ),
            60
        )
    )

    override val dialogPages: Value<ChildPages<*, DialogChild>> =
        MutableValue(ChildPages<HabitatDialogConfig, DialogChild>(items = emptyList(), selectedIndex = 0))

    override fun onBackClick() {}

    override fun onBuildClick() {}

    override fun onHabitatClick(id: String) {}

    override fun onDialogDismiss() {}
}
