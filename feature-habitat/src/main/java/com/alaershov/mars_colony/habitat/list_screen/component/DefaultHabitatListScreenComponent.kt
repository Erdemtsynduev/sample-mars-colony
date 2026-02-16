package com.alaershov.mars_colony.habitat.list_screen.component

import com.alaershov.mars_colony.habitat.HabitatRepository
import com.alaershov.mars_colony.habitat.totalCapacity
import com.alaershov.mars_colony.habitat.build_dialog.HabitatBuildDialogComponent
import com.alaershov.mars_colony.habitat.dismantle_dialog.component.HabitatDismantleDialogComponent
import com.alaershov.mars_colony.habitat.list_screen.DialogChild
import com.alaershov.mars_colony.habitat.list_screen.HabitatDialogConfig
import com.alaershov.mars_colony.habitat.list_screen.HabitatListScreenState
import com.alaershov.mars_colony.message_dialog.MessageDialogState
import com.alaershov.mars_colony.message_dialog.component.MessageDialogComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DefaultHabitatListScreenComponent @AssistedInject internal constructor(
    @Assisted
    componentContext: ComponentContext,
    @Assisted("onBackClick")
    private val onBackClick: () -> Unit,
    private val habitatRepository: HabitatRepository,
    private val habitatBuildDialogComponentFactory: HabitatBuildDialogComponent.Factory,
    private val habitatDismantleDialogComponentFactory: HabitatDismantleDialogComponent.Factory,
    private val messageDialogComponentFactory: MessageDialogComponent.Factory,
) : HabitatListScreenComponent, ComponentContext by componentContext {

    private val _state = MutableStateFlow(
        HabitatListScreenState(
            list = emptyList(),
            totalCapacity = 0
        )
    )

    override val state: StateFlow<HabitatListScreenState> = _state

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val dialogNavigation = SlotNavigation<HabitatDialogConfig>()

    override val dialogSlot: Value<ChildSlot<HabitatDialogConfig, DialogChild>> =
        childSlot(
            source = dialogNavigation,
            serializer = HabitatDialogConfig.serializer(),
            handleBackButton = true,
            key = "HabitatDialogSlot",
            childFactory = ::createDialog,
        )

    init {
        habitatRepository.state
            .onEach { habitatState ->
                _state.value = HabitatListScreenState(
                    list = habitatState.habitatList,
                    totalCapacity = habitatState.totalCapacity
                )
            }
            .launchIn(scope)
    }

    private fun createDialog(
        config: HabitatDialogConfig,
        componentContext: ComponentContext
    ): DialogChild {
        return when (config) {
            HabitatDialogConfig.HabitatBuild -> {
                DialogChild.HabitatBuild(
                    habitatBuildDialogComponentFactory.create(
                        componentContext = componentContext,
                        onDismiss = ::dismissDialog,
                    )
                )
            }

            is HabitatDialogConfig.HabitatDismantle -> {
                DialogChild.HabitatDismantle(
                    habitatDismantleDialogComponentFactory.create(
                        componentContext = componentContext,
                        habitatId = config.habitatId,
                        onConfirmationNeeded = {
                            dialogNavigation.activate(HabitatDialogConfig.ConfirmDismantle(habitatId = config.habitatId))
                        },
                        onDismiss = ::dismissDialog,
                    )
                )
            }

            is HabitatDialogConfig.ConfirmDismantle -> {
                DialogChild.ConfirmDismantle(
                    messageDialogComponentFactory.create(
                        componentContext = componentContext,
                        dialogState = MessageDialogState(
                            message = "Are you sure?",
                            button = "Yes, dismantle!"
                        ),
                        onButtonClick = {
                            habitatRepository.dismantleHabitat(config.habitatId)
                            dismissDialog()
                        },
                        onDismiss = ::dismissDialog,
                    )
                )
            }
        }
    }

    override fun onBackClick() {
        onBackClick.invoke()
    }

    override fun onBuildClick() {
        dialogNavigation.activate(HabitatDialogConfig.HabitatBuild)
    }

    override fun onHabitatClick(id: String) {
        dialogNavigation.activate(HabitatDialogConfig.HabitatDismantle(habitatId = id))
    }

    override fun onDialogDismiss() {
        dismissDialog()
    }

    private fun dismissDialog() {
        dialogNavigation.dismiss()
    }

    @AssistedFactory
    interface Factory : HabitatListScreenComponent.Factory {

        override fun create(
            componentContext: ComponentContext,
            @Assisted("onBackClick")
            onBackClick: () -> Unit,
        ): DefaultHabitatListScreenComponent
    }
}
