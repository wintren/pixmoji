package rocks.wintren.pixmoji.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

@SuppressLint("DiffUtilEquals")
class SimpleBindingAdapter(
    sameItem: ((old: Any, new: Any) -> Boolean)? = null,
    sameContent: ((old: Any, new: Any) -> Boolean)? = null
) : DataBindingAdapter(SimpleDiff(sameItem, sameContent)) {

    private class SimpleDiff(
        val sameItem: ((old: Any, new: Any) -> Boolean)?,
        val sameContent: ((old: Any, new: Any) -> Boolean)?
    ) : DiffUtil.ItemCallback<BindingAdapterItem>() {

        override fun areItemsTheSame(
            oldItem: BindingAdapterItem,
            newItem: BindingAdapterItem
        ): Boolean =
            sameItem?.invoke(oldItem, newItem) ?: (oldItem.model ?: oldItem) == (newItem.model
                ?: newItem)

        override fun areContentsTheSame(
            oldItem: BindingAdapterItem,
            newItem: BindingAdapterItem
        ): Boolean =
            sameContent?.invoke(oldItem, newItem) ?: (oldItem.model ?: oldItem) == (newItem.model
                ?: newItem)
    }

}