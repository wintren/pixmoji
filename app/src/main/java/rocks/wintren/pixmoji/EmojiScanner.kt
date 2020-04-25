package rocks.wintren.pixmoji

import androidx.annotation.ColorInt
import io.reactivex.rxjava3.core.Completable


object EmojiScanner {

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

    private fun getFileNames() = emojiFilesAndNames.keys
    private fun getCategoryName(filename: String): String =
        emojiFilesAndNames.getOrDefault(filename, "N/A")

    var doneImportingEmojis = false
    fun importEmojis() {
        readEmojisFromFiles()
            .subscribe(
                {
                    EmojiRepository.doneAddingEmojis()
                    doneImportingEmojis = true
                },
                { e("EmojiScanner:ImportEmojis", it) }
            )
    }

    sealed class ScanEmojisResult {
        data class InProgress(val category: String, val percent: Int) : ScanEmojisResult()
        data class Finished(val emojis: List<Emoji>)
    }

    private fun readEmojisFromFiles(): Completable {
        return Completable.create { emitter ->

            getFileNames().forEach { filename ->


                w("Read Emojis: $filename")
                val factory = EmojiBitmapFactory(EmojiBitmapFactory.EmojiScale.Small)
                val stream = MojiApp.appContext.assets.open(filename)

                val lines = stream.bufferedReader().readLines()
                val emojis = lines.map {
                    val split = it.split(' ')
                    val emoticon = split.first()
                    val name = split.toMutableList().run {
                        removeAt(0)
                        joinToString(" ")
                    }
                    val emojiBitmap = factory.createEmoji(emoticon)
                    val color = MojiColorUtil.getDominantColor(emojiBitmap)
                    EmojiRepository.addEmoji(color, emoticon)
                    emoticon
                }
                i("ReadEmojis: " + emojis.joinToString(" "))
            }

            emitter.onComplete()
        }.subscribeOn(ioThread)
    }

}

data class EmojiCollection(val name: String, val emojis: List<Emoji>)

data class Emoji(
    val category: String,
    val name: String,
    @ColorInt val colorValue: Int,
    val emoticon: String
) {

    override fun toString(): String {
        return "[$name|$emoticon|$colorValue]"
    }
}