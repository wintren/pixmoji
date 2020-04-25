package rocks.wintren.pixmoji

import androidx.annotation.ColorInt

class ColorEmojiMap {

    val colorEmojiMap: MutableMap<Int, MutableList<Emoji>> = mutableMapOf()
    private val colorIndexCache: MutableMap<Int, Int> = mutableMapOf()

    fun addEmoji(@ColorInt color: Int, emoji: Emoji) {
        colorEmojiMap[color] = colorEmojiMap
            .getOrDefault(color, mutableListOf())
            .apply { add(emoji) }
    }

    fun getEmojisForColor(@ColorInt color: Int): List<Emoji> {
        return colorIndexCache[color]
            ?.let { quickIndex -> colorEmojiMap[quickIndex] }
            ?: closestColor(colorEmojiMap.keys, color)
                .let {
                    colorEmojiMap[it]?.toList()
                        ?: throw RuntimeException("No Emoji, something wrong! ($color)")
                }
    }

    private fun closestColor(colors: Collection<Int>, pixelColor: Int): Int {
        var closestDistance = Double.MAX_VALUE
        var closestColor = 0
        colors.forEach {
            val distance = getDistance(it, pixelColor)
            if (distance < closestDistance) {
                closestDistance = distance
                closestColor = it
            }
        }
        return closestColor
    }

}

