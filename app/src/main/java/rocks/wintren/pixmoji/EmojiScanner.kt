package rocks.wintren.pixmoji

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt

object EmojiScanner {

    fun run(context: Context) {
        readEmojis(context)
        parseColors()
    }

    private fun readEmojis(context: Context) {

        val fileNames = EmojiRepo.getFileNames()
        for (filename in fileNames) {
            val stream = PixMojiApp.appContext.assets.open(filename)
//            val stream = context.assets.open(filename)
            val lines = stream.bufferedReader().readLines()
            val emojis = lines.map {
                val split = it.split(' ')
                val emoticon = split.first()
                val name = split.toMutableList().run {
                    removeAt(0)
                    joinToString(" ")
                }
                Emoji(name, 0, emoticon)
            }
            EmojiRepo.initialiseCollection(filename, emojis)
        }
    }

    private fun parseColors() {
        val factory = EmojiBitmapFactory(EmojiBitmapFactory.EmojiScale.Small)
        val collections = EmojiRepo.getCollections()

        val x= collections.map { collection ->
             collection.name to collection.emojis.map {
                    val emojiBitmap = factory.createEmoji(it.emoticon)
                    val dominantColor = emojiBitmap.getDominantColor()
                    emojiBitmap.recycle()
                    it.copy(colorValue = dominantColor)
                }

        }
        x.forEach { pair ->
            d("------- Collection: ${pair.first}")
            pair.second.forEach { d(it.toString()) }
        }

    }

    private fun Bitmap.getDominantColor(): Int {
        val newBitmap = Bitmap.createScaledBitmap(this, 1, 1, true)
        val color = newBitmap.getPixel(0, 0)
        newBitmap.recycle()
        return color
    }

}

object EmojiRepo {

    private val emojiFilesAndNames = mapOf(
        "people.txt" to "People",
        "activities.txt" to "Activities",
        "flags.txt" to "Flags",
        "food.txt" to "Food",
        "natureAnimals.txt" to "Nature & Animals",
        "objects.txt" to "Objects",
        "symbols.txt" to "Symbols",
        "travel.txt" to "Travel"
    )

    private val collections: MutableMap<String, EmojiCollection> = mutableMapOf()

    fun getFileNames() = emojiFilesAndNames.keys

    fun initialiseCollection(file: String, emojiList: List<Emoji>) {
        val collectionName = emojiFilesAndNames.getValue(file)
        collections[file] = EmojiCollection(collectionName, emojiList)
    }

    fun getCollections(): List<EmojiCollection> {
        return collections.values.toList()
    }

}

data class EmojiCollection(val name: String, val emojis: List<Emoji>)

data class Emoji(
    val name: String,
    @ColorInt val colorValue: Int,
    val emoticon: String
) {

    override fun toString(): String {
        return "[$name|$emoticon|$colorValue]"
    }
}