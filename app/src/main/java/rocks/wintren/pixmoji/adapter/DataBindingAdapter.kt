package rocks.wintren.pixmoji.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL

abstract class DataBindingAdapter(diffCallback: DiffUtil.ItemCallback<BindingAdapterItem>) :
    ListAdapter<BindingAdapterItem, DataBindingViewHolder>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, viewType, parent, false
        )
        return DataBindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder, position: Int) =
        holder.bind(getItem(position))

}

@BindingAdapter("useAdapter", "useManager", requireAll = false)
fun RecyclerView.bindingSetAdapterManager(
    adapter: RecyclerView.Adapter<*>?,
    manager: RecyclerView.LayoutManager?
) {
    setAdapter(adapter)
    layoutManager = manager ?: LinearLayoutManager(context)
}

@BindingAdapter("horizontalAdapter", requireAll = false)
fun RecyclerView.bindingSetHorizontalAdapter(adapter: RecyclerView.Adapter<*>?) {
    setAdapter(adapter)
    layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
}
