package com.alaershov.mars_colony.sheet_stack.component

import android.util.Log
import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import com.alaershov.mars_colony.demo_dialog.component.DemoDialogComponent
import com.alaershov.mars_colony.sheet_stack.bottom_sheet.SheetStackBottomSheetConfig
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackCallback
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.ceil
import kotlin.math.roundToInt

class DefaultSheetStackComponent @AssistedInject internal constructor(
    @Assisted
    componentContext: ComponentContext,
    @Assisted
    mode: SheetStackMode,
    @Assisted("onBackClick")
    private val onBackClick: () -> Unit,
    private val demoDialogComponentFactory: DemoDialogComponent.Factory,
) : SheetStackComponent, ComponentContext by componentContext {

    // TODO потестить размер 19 и больше, чтобы прям до верха экрана
    private val maxSize = 15

    private val _state = MutableStateFlow(
        SheetStackScreenState(
            mode = mode
        )
    )

    override val state: StateFlow<SheetStackScreenState> = _state

    private val bottomSheetPagesNavigation = PagesNavigation<SheetStackBottomSheetConfig>()

    override val dialogPages: Value<ChildPages<*, DialogChild>> =
        childPages(
            source = bottomSheetPagesNavigation,
            serializer = SheetStackBottomSheetConfig.serializer(),
            key = "SheetStackBottomSheetPages",
            pageStatus = ::getSheetStackPageStatus,
            handleBackButton = false,
            childFactory = ::createBottomSheet,
        )

    private fun getSheetStackPageStatus(
        index: Int,
        pages: Pages<SheetStackBottomSheetConfig>
    ): ChildNavState.Status =
        if (index == pages.selectedIndex) ChildNavState.Status.RESUMED else ChildNavState.Status.CREATED

    private val backCallback = BackCallback {
        navigateBack()
    }

    init {
        backHandler.register(backCallback)
        dialogPages.subscribe {
            backCallback.isEnabled = it.items.isNotEmpty()
        }
    }

    private fun navigateBack() {
        if (dialogPages.value.items.isNotEmpty()) {
            bottomSheetPagesNavigation.navigate(
                transformer = { pages ->
                    val newItems =
                        pages.items.takeIf { it.isNotEmpty() }?.dropLast(1) ?: pages.items
                    pages.copy(
                        items = newItems,
                        selectedIndex = (newItems.size - 1).coerceAtLeast(0)
                    )
                },
                onComplete = { _, _ -> }
            )
        } else {
            onBackClick.invoke()
        }
    }

    // баги
    // - размер 17+ дрыгается при разворачивании на весь экран
    // - вставка в начало стека заставляет уже открытый диалог на верхушке стека
    // открыться заново, а новый диалог сзади не анимируется, а открывается сразу весь
    // - удаление нескольких диалогов сразу делает это без анимации, они просто пропадают
    private fun createBottomSheet(
        config: SheetStackBottomSheetConfig,
        componentContext: ComponentContext
    ): DialogChild {
        return when (config) {
            is SheetStackBottomSheetConfig.Sheet -> {
                val size = config.size
                DialogChild.DemoDialog(
                    demoDialogComponentFactory.create(
                    componentContext = componentContext,
                    dialogState = DemoDialogState(
                        size = size,
                        message = "Dialog Size $size @${config.key}",
                        button = "Close"
                    ),
                    onButtonClick = {
                        dismissBottomSheet()
                    },
                    onCloseClick = {
                        dismissBottomSheet()
                    },
                    onCloseAllClick = {
                        log("onCloseAllClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                pages.copy(
                                    items = emptyList(),
                                    selectedIndex = 0
                                )
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onCloseFirstClick = {
                        log("onCloseFirstClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = pages.items.drop(1)
                                pages.copy(
                                    items = newItems,
                                    selectedIndex = (newItems.size - 1).coerceAtLeast(0)
                                )
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onCloseSecondClick = {
                        log("onCloseSecondClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = if (pages.items.size < 2) {
                                    pages.items
                                } else {
                                    pages.items.filterIndexed { index, _ -> index != pages.items.lastIndex - 1 }
                                }
                                pages.copy(
                                    items = newItems,
                                    selectedIndex = (newItems.size - 1).coerceAtLeast(0)
                                )
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onCloseHalfClick = {
                        log("onCloseHalfClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = if (pages.items.isEmpty()) {
                                    pages.items
                                } else {
                                    val middleIndex = ceil(pages.items.size / 2.0).roundToInt()
                                    pages.items.dropLast(middleIndex)
                                }
                                pages.copy(
                                    items = newItems,
                                    selectedIndex = (newItems.size - 1).coerceAtLeast(0)
                                )
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onReplaceClick = {
                        log("onReplaceClick")
                        val newConfig = SheetStackBottomSheetConfig.Sheet(randomSheetSize())
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = pages.items.dropLast(1) + newConfig
                                pages.copy(items = newItems, selectedIndex = newItems.size - 1)
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onAddClick = {
                        log("onAddClick")
                        pushSheet(SheetStackBottomSheetConfig.Sheet(randomSheetSize()))
                    },
                    onAddFirstClick = {
                        log("onAddFirstClick")
                        val config = SheetStackBottomSheetConfig.Sheet(randomSheetSize())
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = listOf(config) + pages.items
                                pages.copy(items = newItems, selectedIndex = newItems.size - 1)
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onAddMiddleClick = {
                        log("onAddMiddleClick")
                        val config = SheetStackBottomSheetConfig.Sheet(randomSheetSize())
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val middleIndex = pages.items.size / 2
                                val firstPart = pages.items.subList(0, middleIndex)
                                val secondPart = pages.items.subList(middleIndex, pages.items.size)
                                val newItems = firstPart + config + secondPart
                                pages.copy(items = newItems, selectedIndex = newItems.size - 1)
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onShuffleClick = {
                        log("onShuffleClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = pages.items.shuffled()
                                pages.copy(
                                    items = newItems,
                                    selectedIndex = (newItems.size - 1).coerceAtLeast(0)
                                )
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onShiftForwardClick = {
                        log("onShiftForwardClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = if (pages.items.isEmpty()) {
                                    pages.items
                                } else {
                                    listOf(pages.items.last()) + pages.items.dropLast(1)
                                }
                                pages.copy(
                                    items = newItems,
                                    selectedIndex = (newItems.size - 1).coerceAtLeast(0)
                                )
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                    onShiftBackwardClick = {
                        log("onShiftBackwardClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { pages ->
                                val newItems = if (pages.items.isEmpty()) {
                                    pages.items
                                } else {
                                    pages.items.drop(1) + listOf(pages.items.first())
                                }
                                pages.copy(
                                    items = newItems,
                                    selectedIndex = (newItems.size - 1).coerceAtLeast(0)
                                )
                            },
                            onComplete = { _, _ -> }
                        )
                    },
                )
                )
            }
        }
    }

    override fun onBottomSheetPagesDismiss() {
        dismissBottomSheet()
    }

    override fun onBottomSheetPagesDismiss(config: Any) {
        bottomSheetPagesNavigation.navigate(
            transformer = { pages ->
                val newItems = pages.items.filter { it != config }
                val selectedIndex = if (newItems.isEmpty()) {
                    0
                } else {
                    val selected = pages.items.getOrNull(pages.selectedIndex)
                    if (selected != null && selected in newItems) {
                        newItems.indexOf(selected).coerceAtLeast(0)
                    } else {
                        (newItems.size - 1).coerceAtLeast(0)
                    }
                }
                pages.copy(items = newItems, selectedIndex = selectedIndex)
            },
            onComplete = { _, _ -> }
        )
    }

    override fun onBackClick() {
        navigateBack()
    }

    override fun onOpenSingleDialogClick() {
        pushSheet(SheetStackBottomSheetConfig.Sheet(randomSheetSize()))
    }

    override fun onOpenFewDialogsClick() {
        val sizeList = List(3) { randomSheetSize() }.sortedDescending()
        for (size in sizeList) {
            pushSheet(SheetStackBottomSheetConfig.Sheet(size))
        }
    }

    override fun onOpenManyDialogsClick() {
        val sizeList = List(8) { randomSheetSize() }.sortedDescending()
        for (size in sizeList) {
            pushSheet(SheetStackBottomSheetConfig.Sheet(size))
        }
    }

    private fun pushSheet(configuration: SheetStackBottomSheetConfig) {
        bottomSheetPagesNavigation.navigate(
            transformer = { pages ->
                val newItems = if (pages.items.lastOrNull() == configuration) {
                    pages.items
                } else {
                    pages.items + configuration
                }
                pages.copy(items = newItems, selectedIndex = newItems.size - 1)
            },
            onComplete = { _, _ -> }
        )
    }

    private fun randomSheetSize(): Int {
        return (1..maxSize).random()
    }

    private fun dismissBottomSheet() {
        log("dismissBottomSheet")
        bottomSheetPagesNavigation.navigate(
            transformer = { pages ->
                val newItems = pages.items.takeIf { it.isNotEmpty() }?.dropLast(1) ?: pages.items
                pages.copy(items = newItems, selectedIndex = (newItems.size - 1).coerceAtLeast(0))
            },
            onComplete = { _, _ -> }
        )
    }

    private fun log(message: String) {
        Log.d("DefaultSheetStackComponent", message)
    }

    @AssistedFactory
    interface Factory : SheetStackComponent.Factory {

        override fun create(
            componentContext: ComponentContext,
            mode: SheetStackMode,
            @Assisted("onBackClick")
            onBackClick: () -> Unit,
        ): DefaultSheetStackComponent
    }
}
