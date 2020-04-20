package rocks.wintren.pixmoji.adapter

import androidx.annotation.LayoutRes

data class BindingAdapterItem(
    @LayoutRes val layout: Int,
    val variableId: Int? = null,
    val model: Any? = null
)