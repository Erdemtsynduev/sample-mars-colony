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

    private var testDismantleCount = 0

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
                            thirdButton = "Open dismantle again (test stack)",
                        ),
                        onButtonClick = {
                            habitatRepository.dismantleHabitat(config.habitatId)
                            dismissAllDialogsForHabitat(config.habitatId)
                        },
                        onSecondButtonClick = ::dismissDialogsBehind,
                        onThirdButtonClick = {
                            testDismantleCount++
                            pushDialog(HabitatDialogConfig.HabitatDismantle(habitatId = "${config.habitatId}-$testDismantleCount"))
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

    // ========== НОВЫЕ МЕТОДЫ ДЛЯ УДАЛЕНИЯ ПО ТИПУ ==========

    /**
     * Удаляет первый диалог указанного типа из стека
     */
    private inline fun <reified T : HabitatDialogConfig> dismissDialogByType() {
        dialogNavigation.navigate(
            transformer = { pages ->
                val indexToRemove = pages.items.indexOfFirst { it is T }
                if (indexToRemove == -1) {
                    pages // Диалог данного типа не найден
                } else {
                    val newItems = pages.items.toMutableList().apply { removeAt(indexToRemove) }
                    val newSelectedIndex = calculateNewSelectedIndex(
                        oldIndex = pages.selectedIndex,
                        removedIndex = indexToRemove,
                        newSize = newItems.size
                    )
                    pages.copy(items = newItems, selectedIndex = newSelectedIndex)
                }
            },
            onComplete = { _, _ -> }
        )
    }

    /**
     * Удаляет все диалоги указанного типа из стека
     */
    private inline fun <reified T : HabitatDialogConfig> dismissAllDialogsByType() {
        dialogNavigation.navigate(
            transformer = { pages ->
                val newItems = pages.items.filterNot { it is T }
                if (newItems.size == pages.items.size) {
                    pages // Ничего не удалено
                } else {
                    val newSelectedIndex = calculateNewSelectedIndexAfterFilter(
                        pages = pages,
                        newItems = newItems
                    )
                    pages.copy(items = newItems, selectedIndex = newSelectedIndex)
                }
            },
            onComplete = { _, _ -> }
        )
    }

    /**
     * Удаляет диалоги по условию (предикату)
     */
    private fun dismissDialogsWhere(predicate: (HabitatDialogConfig) -> Boolean) {
        dialogNavigation.navigate(
            transformer = { pages ->
                val newItems = pages.items.filterNot(predicate)
                if (newItems.size == pages.items.size) {
                    pages // Ничего не удалено
                } else {
                    val newSelectedIndex = calculateNewSelectedIndexAfterFilter(
                        pages = pages,
                        newItems = newItems
                    )
                    pages.copy(items = newItems, selectedIndex = newSelectedIndex)
                }
            },
            onComplete = { _, _ -> }
        )
    }

    /**
     * Вспомогательная функция для расчёта нового selectedIndex при удалении одного элемента
     */
    private fun calculateNewSelectedIndex(
        oldIndex: Int,
        removedIndex: Int,
        newSize: Int
    ): Int {
        return when {
            newSize == 0 -> 0
            oldIndex > removedIndex -> oldIndex - 1
            oldIndex == removedIndex -> (newSize - 1).coerceAtLeast(0)
            else -> oldIndex
        }.coerceIn(0, (newSize - 1).coerceAtLeast(0))
    }

    /**
     * Вспомогательная функция для расчёта нового selectedIndex после фильтрации
     */
    private fun calculateNewSelectedIndexAfterFilter(
        pages: Pages<HabitatDialogConfig>,
        newItems: List<HabitatDialogConfig>
    ): Int {
        return if (newItems.isEmpty()) {
            0
        } else {
            val selectedItem = pages.items.getOrNull(pages.selectedIndex)
            if (selectedItem != null && selectedItem in newItems) {
                // Выбранный элемент остался, находим его новый индекс
                newItems.indexOf(selectedItem).coerceAtLeast(0)
            } else {
                // Выбранный элемент удалён, выбираем последний
                (newItems.size - 1).coerceAtLeast(0)
            }
        }
    }

    // ========== ПУБЛИЧНЫЕ МЕТОДЫ ДЛЯ ВНЕШНЕГО ИСПОЛЬЗОВАНИЯ ==========

    /**
     * Удаляет первый диалог типа HabitatBuild
     */
    fun dismissHabitatBuildDialog() {
        dismissDialogByType<HabitatDialogConfig.HabitatBuild>()
    }

    /**
     * Удаляет первый диалог типа HabitatDismantle
     */
    fun dismissHabitatDismantleDialog() {
        dismissDialogByType<HabitatDialogConfig.HabitatDismantle>()
    }

    /**
     * Удаляет первый диалог типа ConfirmDismantle
     */
    fun dismissConfirmDismantleDialog() {
        dismissDialogByType<HabitatDialogConfig.ConfirmDismantle>()
    }

    /**
     * Удаляет все диалоги типа HabitatDismantle
     */
    fun dismissAllHabitatDismantleDialogs() {
        dismissAllDialogsByType<HabitatDialogConfig.HabitatDismantle>()
    }

    /**
     * Удаляет диалог HabitatDismantle с конкретным habitatId
     */
    fun dismissHabitatDismantleDialogById(habitatId: String) {
        dismissDialogsWhere { config ->
            config is HabitatDialogConfig.HabitatDismantle && config.habitatId == habitatId
        }
    }

    /**
     * Удаляет диалог ConfirmDismantle с конкретным habitatId
     */
    fun dismissConfirmDismantleDialogById(habitatId: String) {
        dismissDialogsWhere { config ->
            config is HabitatDialogConfig.ConfirmDismantle && config.habitatId == habitatId
        }
    }

    /**
     * Удаляет все диалоги связанные с конкретным habitatId
     */
    fun dismissAllDialogsForHabitat(habitatId: String) {
        dismissDialogsWhere { config ->
            when (config) {
                is HabitatDialogConfig.HabitatDismantle -> config.habitatId == habitatId
                is HabitatDialogConfig.ConfirmDismantle -> config.habitatId == habitatId
                else -> false
            }
        }
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
