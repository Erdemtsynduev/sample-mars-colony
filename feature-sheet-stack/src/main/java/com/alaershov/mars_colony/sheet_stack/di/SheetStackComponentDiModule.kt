package com.alaershov.mars_colony.sheet_stack.di

import com.alaershov.mars_colony.sheet_stack.component.DefaultSheetStackComponent
import com.alaershov.mars_colony.sheet_stack.component.SheetStackComponent
import dagger.Binds
import dagger.Module

@Module
interface SheetStackComponentDiModule {

    @Binds
    fun bindSheetStackComponent(
        impl: DefaultSheetStackComponent.Factory,
    ): SheetStackComponent.Factory
}
