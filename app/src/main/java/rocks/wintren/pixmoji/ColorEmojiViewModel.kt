package rocks.wintren.pixmoji

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import rocks.wintren.pixmoji.adapter.BindingAdapterItem
import rocks.wintren.pixmoji.adapter.SimpleBindingAdapter

class ColorEmojiViewModel : BaseViewModel() {

    val colorAdapter = SimpleBindingAdapter()
    private var started: Boolean = false

    fun start() {
        if (started) return
        started = true

//        readEmojis()

    }

    private fun readEmojis() {
        EmojiScanner.readEmojis()
            .observeOn(computationThread)
            .map { list ->
                val colorComparator = ColorComparator()
                list.sortedWith(Comparator { e1, e2 ->
                    colorComparator.compare(
                        e1.colorValue,
                        e2.colorValue
                    )
                })
            }
            .map { list ->
                list.map { EmojiDisplayColumnItem(it.colorValue, listOf(it.emoticon)) }
            }
            .map { list ->
                list.map { BindingAdapterItem(R.layout.item_color_pixel, BR.emojiColumn, it) }
            }
            .observeOn(mainThread)
            .subscribe({ list ->
                colorAdapter.submitList(list)
            }, {
                it.printStackTrace()
            })
    }

}

class ColorComparator : Comparator<Int> {

    override fun compare(color1: Int, color2: Int): Int {

        val hsl1 = color1.getHSL()
        val hsl2 = color2.getHSL()

        // Colors have the same Hue
        return if (hsl1[0] == hsl2[0]) {

            // Colors have the same saturation
            if (hsl1[1] == hsl2[1]) {
                // Compare lightness
                (hsl1[2] - hsl2[2]).toInt()

            } else {
                (hsl1[1] - hsl2[1]).toInt()
            }

        } else {
            (hsl1[0] - hsl2[0]).toInt()
        }
    }

    private fun Int.getHSL(): FloatArray {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(this, hsl)
        for (i in hsl.indices) {
            hsl[i] = hsl[i] * 100
        }
        return hsl
    }
}