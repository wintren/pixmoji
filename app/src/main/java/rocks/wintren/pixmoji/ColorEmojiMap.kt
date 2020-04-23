package rocks.wintren.pixmoji

import androidx.annotation.ColorInt

class ColorEmojiMap {

    private var finalised = false

    private lateinit var referenceMap: MutableMap<IntRange, Int>
    private val colorEmojiMap: MutableMap<Int, MutableList<String>> = mutableMapOf()

    fun addEmoji(@ColorInt color: Int, emoji: String) {
        if (finalised) throw RuntimeException("Can't put values into a finalised FillerMap")
        colorEmojiMap[color] = colorEmojiMap.getOrDefault(color, mutableListOf()).apply { add(emoji) }
    }

    fun finalise() {
        finalised = true
        referenceMap = mutableMapOf()

        colorEmojiMap.keys.reduce { left, right ->
            val diff = right - left
            val halfwayBetween = (left + (diff / 2f)).toInt()

            val leftHalf = left..halfwayBetween
            val rightHalf = halfwayBetween..right

            referenceMap[leftHalf] = left
            referenceMap[rightHalf] = right

            right
        }

    }

    fun getClosest(@ColorInt color: Int): List<String> {
        if (!this::referenceMap.isInitialized) {
            throw RuntimeException("Trying to call get Closest before finalising map.")
        }

        return colorEmojiMap[color]
            ?: referenceMap.keys.firstOrNull { range -> color in range }
                ?.let { range -> referenceMap[range] }
                ?.let { shiftedKey -> colorEmojiMap[shiftedKey] }
            ?: throw RuntimeException("Key $color was not present in map")
    }
}