package com.alaershov.mars_colony.sheet_stack.component

import android.util.Log
import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.bottomSheetPages
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.navigate
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.pop
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.pushNew
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.replaceAll
import com.alaershov.mars_colony.demo_dialog.DemoDialogState
import com.alaershov.mars_colony.demo_dialog.component.DemoDialogComponent
import com.alaershov.mars_colony.sheet_stack.bottom_sheet.SheetStackBottomSheetConfig
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.value.Value
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

    private val maxSize = 20

    private val _state = MutableStateFlow(
        SheetStackScreenState(
            mode = mode
        )
    )

    override val state: StateFlow<SheetStackScreenState> = _state

    private val bottomSheetPagesNavigation = PagesNavigation<SheetStackBottomSheetConfig>()

    override val bottomSheetPages: Value<ChildPages<*, BottomSheetContentComponent>> =
        bottomSheetPages(
            source = bottomSheetPagesNavigation,
            serializer = SheetStackBottomSheetConfig.serializer(),
            childFactory = ::createBottomSheet,
        )

    // баги
    // - размер 17+ дрыгается при разворачивании на весь экран
    // - вставка в начало стека заставляет уже открытый диалог на верхушке стека
    // открыться заново, а новый диалог сзади не анимируется, а открывается сразу весь
    // - удаление нескольких диалогов сразу делает это без анимации, они просто пропадают
    private fun createBottomSheet(
        config: SheetStackBottomSheetConfig,
        componentContext: ComponentContext
    ): BottomSheetContentComponent {
        return when (config) {
            is SheetStackBottomSheetConfig.Sheet -> {
                val size = config.size
                demoDialogComponentFactory.create(
                    componentContext = componentContext,
                    dialogState = DemoDialogState(
                        size = size,
                        message = "Dialog Size $size",
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
                        bottomSheetPagesNavigation.replaceAll()
                    },
                    onCloseFirstClick = {
                        log("onCloseFirstClick")
                        bottomSheetPagesNavigation.navigate { items ->
                            items.drop(1)
                        }
                    },
                    onCloseRandomClick = {
                        log("onCloseRandomClick")
                        bottomSheetPagesNavigation.navigate(
                            transformer = { items ->
                                if (items.isEmpty()) {
                                    items
                                } else {
                                    val randomItem = items.random()
                                    items.filter { item -> item != randomItem }
                                }
                            }
                        )
                    },
                    onCloseHalfClick = {
                        log("onCloseHalfClick")
                        bottomSheetPagesNavigation.navigate { items ->
                            if (items.isEmpty()) {
                                items
                            } else {
                                items.dropLast(ceil(items.size / 2.0).roundToInt())
                            }
                        }
                    },
                    onAddClick = {
                        log("onAddClick")
                        bottomSheetPagesNavigation.pushNew(
                            SheetStackBottomSheetConfig.Sheet(randomSheetSize())
                        )
                    },
                    onAddFirstClick = {
                        log("onAddFirstClick")
                        val config = SheetStackBottomSheetConfig.Sheet(randomSheetSize())
                        bottomSheetPagesNavigation.navigate { items ->
                            listOf(config) + items
                        }
                    },
                    onAddMiddleClick = {
                        log("onAddMiddleClick")
                        val config = SheetStackBottomSheetConfig.Sheet(randomSheetSize())
                        bottomSheetPagesNavigation.navigate { items ->
                            val middleIndex = items.size / 2
                            val firstPart = items.subList(0, middleIndex)
                            val secondPart = items.subList(middleIndex, items.size)
                            firstPart + config + secondPart
                        }
                    },
                    onShuffleClick = {
                        log("onShuffleClick")
                        bottomSheetPagesNavigation.navigate { items ->
                            items.shuffled()
                        }
                    },
                    onShiftForwardClick = {
                        log("onShiftForwardClick")
                        bottomSheetPagesNavigation.navigate { items ->
                            if (items.isEmpty()) {
                                items
                            } else {
                                listOf(items.last()) + items.dropLast(1)
                            }
                        }
                    },
                    onShiftBackwardClick = {
                        log("onShiftBackwardClick")
                        bottomSheetPagesNavigation.navigate { items ->
                            if (items.isEmpty()) {
                                items
                            } else {
                                items.drop(1) + listOf(items.first())
                            }
                        }
                    },
                )
            }
        }
    }

    override fun onBottomSheetPagesDismiss() {
        dismissBottomSheet()
    }

    override fun onBottomSheetPagesDismiss(config: Any) {
        bottomSheetPagesNavigation.navigate { items ->
            items.filter { it != config }
        }
    }

    override fun onBackClick() {
        onBackClick.invoke()
    }

    override fun onOpenSingleDialogClick() {
        bottomSheetPagesNavigation.pushNew(
            SheetStackBottomSheetConfig.Sheet(randomSheetSize())
        )
    }

    override fun onOpenFewDialogsClick() {
        val sizeList = List(3) {
            randomSheetSize()
        }.sortedDescending()
        for (size in sizeList) {
            bottomSheetPagesNavigation.pushNew(
                SheetStackBottomSheetConfig.Sheet(size)
            )
        }
    }

    override fun onOpenManyDialogsClick() {
        val sizeList = List(8) {
            randomSheetSize()
        }.sortedDescending()
        for (size in sizeList) {
            bottomSheetPagesNavigation.pushNew(
                SheetStackBottomSheetConfig.Sheet(size)
            )
        }
    }

    private fun randomSheetSize(): Int {
        return (1..maxSize).random()
    }

    private fun dismissBottomSheet() {
        log("dismissBottomSheet")
        bottomSheetPagesNavigation.pop()
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
