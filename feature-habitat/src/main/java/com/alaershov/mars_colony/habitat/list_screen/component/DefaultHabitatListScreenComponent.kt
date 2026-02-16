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
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
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

    private val dialogNavigation = PagesNavigation<HabitatDialogConfig>()

    override val dialogPages: Value<ChildPages<HabitatDialogConfig, DialogChild>> =
        childPages(
            source = dialogNavigation,
            serializer = HabitatDialogConfig.serializer(),
            key = "HabitatDialogPages",
            pageStatus = ::getDialogPageStatus,
            handleBackButton = true,
            childFactory = ::createDialog,
        )

    private fun getDialogPageStatus(index: Int, pages: Pages<HabitatDialogConfig>): ChildNavState.Status =
        if (index == pages.selectedIndex) ChildNavState.Status.RESUMED else ChildNavState.Status.CREATED

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
                            pushDialog(HabitatDialogConfig.ConfirmDismantle(habitatId = config.habitatId))
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
                            button = "Yes, dismantle!",
                            secondButton = "Remove dialogs behind",
                        ),
                        onButtonClick = {
                            habitatRepository.dismantleHabitat(config.habitatId)
                            dismissDialogs(2)
                        },
                        onSecondButtonClick = ::dismissDialogsBehind,
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
        pushDialog(HabitatDialogConfig.HabitatBuild)
    }

    override fun onHabitatClick(id: String) {
        pushDialog(HabitatDialogConfig.HabitatDismantle(habitatId = id))
    }

    override fun onDialogDismiss() {
        dismissDialog()
    }

    private fun pushDialog(configuration: HabitatDialogConfig) {
        dialogNavigation.navigate(
            transformer = { pages ->
                val newItems = if (pages.items.lastOrNull() == configuration) pages.items else pages.items + configuration
                pages.copy(items = newItems, selectedIndex = newItems.size - 1)
            },
            onComplete = { _, _ -> }
        )
    }

    private fun dismissDialog() {
        dialogNavigation.navigate(
            transformer = { pages ->
                val newItems = pages.items.takeIf { it.isNotEmpty() }?.dropLast(1) ?: pages.items
                pages.copy(items = newItems, selectedIndex = (newItems.size - 1).coerceAtLeast(0))
            },
            onComplete = { _, _ -> }
        )
    }

    private fun dismissDialogs(count: Int) {
        dialogNavigation.navigate(
            transformer = { pages ->
                val newItems = pages.items.dropLast(count)
                pages.copy(items = newItems, selectedIndex = (newItems.size - 1).coerceAtLeast(0))
            },
            onComplete = { _, _ -> }
        )
    }

    private fun dismissDialogsBehind() {
        dialogNavigation.navigate(
            transformer = { pages ->
                val top = pages.items.lastOrNull()
                pages.copy(items = listOfNotNull(top), selectedIndex = 0)
            },
            onComplete = { _, _ -> }
        )
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
