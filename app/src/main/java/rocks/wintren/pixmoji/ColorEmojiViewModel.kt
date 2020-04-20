package rocks.wintren.pixmoji

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import rocks.wintren.pixmoji.adapter.SimpleBindingAdapter
import rocks.wintren.pixmoji.adapter.BindingAdapterItem

class ColorEmojiViewModel : BaseViewModel() {

    val colorAdapter = SimpleBindingAdapter()

    init {

        val colorRange = (Color.BLACK..Color.WHITE step 100_000).toList()
        i("ColorRange: ${colorRange.size}")
        val colorPixels = colorRange.map {
            i("color: $it")
            BindingAdapterItem(R.layout.item_color_pixel, BR.color, it)
        }
        colorAdapter.submitList(colorPixels)
    }

}