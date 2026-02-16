package com.alaershov.mars_colony.message_dialog

data class MessageDialogState(
    val message: String,
    val button: String,
    val secondButton: String? = null,
    val thirdButton: String? = null,
)
