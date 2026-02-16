package com.alaershov.mars_colony.sheet_stack.component

import com.alaershov.mars_colony.demo_dialog.component.DemoDialogComponent

sealed class DialogChild {

    data class DemoDialog(val component: DemoDialogComponent) : DialogChild()
}
