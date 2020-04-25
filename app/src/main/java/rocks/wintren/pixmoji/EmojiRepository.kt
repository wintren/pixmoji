package rocks.wintren.pixmoji

import io.reactivex.rxjava3.subjects.BehaviorSubject

object EmojiRepository {

    val colorEmojiMap = ColorEmojiMap()

    val ready = BehaviorSubject.create<Boolean>().apply { onNext(false) }

    fun addEmoji(color: Int, emoji: Emoji) {
        i("$emoji added to Repository ($color)")
        colorEmojiMap.addEmoji(color, emoji)
    }

    fun getEmoji(color: Int): Emoji {
        return colorEmojiMap.getEmojisForColor(color).random()
    }

    fun doneAddingEmojis() {
        ready.onNext(true)
    }

}