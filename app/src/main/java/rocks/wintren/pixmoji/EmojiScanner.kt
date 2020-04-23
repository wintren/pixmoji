package rocks.wintren.pixmoji

import androidx.annotation.ColorInt
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


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

    fun run(): Completable {
        return Completable.create { emitter ->
            readEmojis()
            emitter.onComplete()
        }.subscribeOn(ioThread)
    }

    sealed class ScanEmojisResult {
        data class InProgress(val category: String, val percent: Int) : ScanEmojisResult()
        data class Finished(val emojis: List<Emoji>)
    }

    fun readEmojis(): Observable<List<Emoji>> {
        return Observable.just(getFileNames())
            .observeOn(ioThread)
            .flatMapIterable { it }
            .flatMap { readEmojisForFile(it).toObservable() }

    }

    private fun readEmojisForFile(filename: String): Single<List<Emoji>> {
        return Single.create { emitter ->
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
                val color = EmojiColor.emojiColor(emojiBitmap)
                d("Added to $filename: $emoticon $color $name")
                Emoji(filename, name, color, emoticon)
            }
//            d("Emojis: ${emojis.toTypedArray()}")
            EmojiRepo.collections.add(EmojiCollection(getCategoryName(filename), emojis))
            emitter.onSuccess(emojis)
        }
    }

}

object EmojiRepo {

    val collections: MutableList<EmojiCollection> = mutableListOf()

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