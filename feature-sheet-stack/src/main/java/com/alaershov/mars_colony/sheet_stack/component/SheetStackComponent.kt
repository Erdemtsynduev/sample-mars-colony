package com.alaershov.mars_colony.sheet_stack.component

import com.arkivanov.decompose.ComponentContext

interface SheetStackComponent {

    interface Factory {

        fun create(
            componentContext: ComponentContext,
        ): SheetStackComponent
    }
}
