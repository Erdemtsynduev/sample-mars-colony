package com.alaershov.mars_colony.habitat.dismantle_dialog.component

import com.alaershov.mars_colony.habitat.Habitat
import com.alaershov.mars_colony.habitat.dismantle_dialog.HabitatDismantleDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PreviewHabitatDismantleDialogComponent : HabitatDismantleDialogComponent {

    override val state: StateFlow<HabitatDismantleDialogState> = MutableStateFlow(
        HabitatDismantleDialogState(
            habitat = Habitat(
                id = "1111-1111-1111-1111",
                capacity = 15,
            ),
            capacityCurrent = 60,
            capacityAfterDismantle = 45,
        )
    )

    override fun onDismantleClick() {}

    override fun onDismiss() {}
}
