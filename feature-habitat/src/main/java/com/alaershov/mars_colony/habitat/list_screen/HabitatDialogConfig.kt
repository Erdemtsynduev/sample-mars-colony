package com.alaershov.mars_colony.habitat.list_screen

import kotlinx.serialization.Serializable

@Serializable
sealed class HabitatDialogConfig {

    @Serializable
    data object HabitatBuild : HabitatDialogConfig()

    @Serializable
    data class HabitatDismantle(
        val habitatId: String
    ) : HabitatDialogConfig()

    @Serializable
    data class ConfirmDismantle(
        val habitatId: String
    ) : HabitatDialogConfig()
}
