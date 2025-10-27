package com.alaershov.mars_colony.demo_dialog.di

import com.alaershov.mars_colony.demo_dialog.component.DefaultDemoDialogComponent
import com.alaershov.mars_colony.demo_dialog.component.DemoDialogComponent
import dagger.Binds
import dagger.Module

@Module
interface DemoDialogComponentDiModule {

    @Binds
    fun demoDialogComponentFactory(
        impl: DefaultDemoDialogComponent.Factory
    ): DemoDialogComponent.Factory
}
