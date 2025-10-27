package com.alaershov.mars_colony.sheet_stack.component

import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.bottomSheetPages
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.pop
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.popRandom
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

class DefaultSheetStackComponent @AssistedInject internal constructor(
    @Assisted
    componentContext: ComponentContext,
    private val demoDialogComponentFactory: DemoDialogComponent.Factory,
) : SheetStackComponent, ComponentContext by componentContext {

    private val maxSize = 20

    private val bottomSheetPagesNavigation = PagesNavigation<SheetStackBottomSheetConfig>()

    override val bottomSheetPages: Value<ChildPages<*, BottomSheetContentComponent>> =
        bottomSheetPages(
            source = bottomSheetPagesNavigation,
            serializer = SheetStackBottomSheetConfig.serializer(),
            childFactory = ::createBottomSheet,
        )

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
                        bottomSheetPagesNavigation.replaceAll()
                    },
                    onCloseRandomClick = {
                        bottomSheetPagesNavigation.popRandom()
                    },
                )
            }
        }
    }

    override fun onBottomSheetPagesDismiss() {
        dismissBottomSheet()
    }

    override fun onOpenSingleDialogClick() {
        bottomSheetPagesNavigation.pushNew(
            SheetStackBottomSheetConfig.Sheet(1)
        )
    }

    override fun onOpenFewDialogsClick() {
        val sizeList = List(3) {
            (1..maxSize).random()
        }.sortedDescending()
        for (size in sizeList) {
            bottomSheetPagesNavigation.pushNew(
                SheetStackBottomSheetConfig.Sheet(size)
            )
        }
    }

    override fun onOpenManyDialogsClick() {
        val sizeList = List(8) {
            (1..maxSize).random()
        }.sortedDescending()
        for (size in sizeList) {
            bottomSheetPagesNavigation.pushNew(
                SheetStackBottomSheetConfig.Sheet(size)
            )
        }
    }

    private fun dismissBottomSheet() {
        bottomSheetPagesNavigation.pop()
    }

    @AssistedFactory
    interface Factory : SheetStackComponent.Factory {

        override fun create(
            componentContext: ComponentContext,
        ): DefaultSheetStackComponent
    }
}
