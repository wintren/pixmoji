package rocks.wintren.pixmoji

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.BehaviorSubject

object EmojiRepository {

    val colorEmojiMap = ColorEmojiMap()

    val ready = BehaviorSubject.create<Boolean>().apply { onNext(false) }

    fun addEmoji(color: Int, emoji: String) {
        i("$emoji added to Repository ($color)")
        colorEmojiMap.addEmoji(color, emoji)
    }

    fun getEmoji(color: Int): String {
        return colorEmojiMap.getEmojisForColor(color).random()
    }

    fun doneAddingEmojis() {
        ready.onNext(true)
    }

}