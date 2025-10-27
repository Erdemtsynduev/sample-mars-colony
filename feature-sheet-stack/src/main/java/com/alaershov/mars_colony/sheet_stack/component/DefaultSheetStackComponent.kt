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
                demoDialogComponentFactory.create(
                    componentContext = componentContext,
                    dialogState = DemoDialogState(
                        message = "Message",
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
            SheetStackBottomSheetConfig.Sheet(
                listOf(
                    "List Item"
                )
            )
        )
    }

    override fun onOpenFewDialogsClick() {
        for (i in 1..3) {
            bottomSheetPagesNavigation.pushNew(
                SheetStackBottomSheetConfig.Sheet(
                    listOf(
                        "List Item $i"
                    )
                )
            )
        }
    }

    override fun onOpenManyDialogsClick() {
        for (i in 1..10) {
            bottomSheetPagesNavigation.pushNew(
                SheetStackBottomSheetConfig.Sheet(
                    listOf(
                        "List Item $i"
                    )
                )
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
