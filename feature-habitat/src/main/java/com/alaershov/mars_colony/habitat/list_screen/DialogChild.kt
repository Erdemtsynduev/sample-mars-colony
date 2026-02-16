package com.alaershov.mars_colony.habitat.list_screen

import com.alaershov.mars_colony.habitat.build_dialog.HabitatBuildDialogComponent
import com.alaershov.mars_colony.habitat.dismantle_dialog.component.HabitatDismantleDialogComponent
import com.alaershov.mars_colony.message_dialog.component.MessageDialogComponent

sealed class DialogChild {

    data class HabitatBuild(val component: HabitatBuildDialogComponent) : DialogChild()

    data class HabitatDismantle(val component: HabitatDismantleDialogComponent) : DialogChild()

    data class ConfirmDismantle(val component: MessageDialogComponent) : DialogChild()
}
