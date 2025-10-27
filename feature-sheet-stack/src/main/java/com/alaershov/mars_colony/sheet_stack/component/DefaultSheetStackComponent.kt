package com.alaershov.mars_colony.sheet_stack.component

import com.alaershov.mars_colony.bottom_sheet.BottomSheetContentComponent
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.bottomSheetPages
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.pop
import com.alaershov.mars_colony.bottom_sheet.material3.pages.navigation.pushNew
import com.alaershov.mars_colony.message_dialog.MessageDialogState
import com.alaershov.mars_colony.message_dialog.component.MessageDialogComponent
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
    private val messageDialogComponentFactory: MessageDialogComponent.Factory,
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
            SheetStackBottomSheetConfig.Sheet -> {
                messageDialogComponentFactory.create(
                    componentContext = componentContext,
                    dialogState = MessageDialogState(
                        message = "Message",
                        button = "Close"
                    ),
                    onButtonClick = {
                        dismissBottomSheet()
                    },
                )
            }
        }
    }

    override fun onBottomSheetPagesDismiss() {
        dismissBottomSheet()
    }

    override fun onOpenSingleDialogClick() {
        bottomSheetPagesNavigation.pushNew(SheetStackBottomSheetConfig.Sheet)
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
