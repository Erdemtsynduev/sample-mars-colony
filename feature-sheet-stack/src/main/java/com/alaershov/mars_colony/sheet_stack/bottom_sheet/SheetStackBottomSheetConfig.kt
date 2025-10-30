package com.alaershov.mars_colony.sheet_stack.bottom_sheet

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Serializable
sealed class SheetStackBottomSheetConfig {

    @Serializable
    data class Sheet(
        val size: Int,
        val key: String = Uuid.random().hashCode().toHexString(),
    ) : SheetStackBottomSheetConfig()
}
