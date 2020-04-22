package rocks.wintren.pixmoji.adapter

import android.content.ContextWrapper
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

class DataBindingViewHolder(private val binding: ViewDataBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BindingAdapterItem) {
        if (item.variableId != null && item.model != null)
            binding.setVariable(item.variableId, item.model)
        binding.lifecycleOwner = itemView.getLifecycleOwner()
        binding.executePendingBindings()
    }

}

fun View.getLifecycleOwner(): LifecycleOwner {
    var context = context
    while (context !is LifecycleOwner) {
        context = (context as ContextWrapper).baseContext
    }
    return context
}