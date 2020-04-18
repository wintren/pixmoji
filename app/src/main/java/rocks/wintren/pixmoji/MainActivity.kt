package rocks.wintren.pixmoji

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import rocks.wintren.pixmoji.EmojiBitmapFactory.CreateArtworkUpdate.InProgress
import rocks.wintren.pixmoji.EmojiBitmapFactory.CreateArtworkUpdate.Success

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val file = "link.jpg"
        val emojiScale = EmojiBitmapFactory.EmojiScale.Tiny
        val imageScale = 6_000
        // 10_000 | 01:12:061
        // 20_000 | 05:01:198 Looks really ugly, emojis start re-appearing

        runEmojiArt(file, emojiScale, imageScale)
    }

    @SuppressLint("SetTextI18n")
    private fun runEmojiArt(
        file: String,
        emojiScale: EmojiBitmapFactory.EmojiScale,
        imageScale: Int
    ) {
        originalImage.setImageDrawable(getDrawableFromAssets(file))
        val drawable = getDrawableFromAssets(file)
        val emojiFactory = EmojiBitmapFactory(this, emojiScale)
        emojiFactory.createArtwork(drawable, emojiScale, imageScale)
            .observeOn(mainScheduler)
            .subscribe { update ->
                when (update) {
                    is Success -> {
                        progressLabel.visibility = View.GONE
                        progressLabel.text = null
                        loadingSpinner.visibility = View.GONE
                        emojiImage.setImageBitmap(update.artwork)
                    }
                    is InProgress -> {
                        progressLabel.visibility = View.VISIBLE
                        loadingSpinner.visibility = View.VISIBLE
                        progressLabel.text = "${update.percentage}%"
                    }
                }

            }

    }

    private fun measureEmojis() {
        val factory = EmojiBitmapFactory(this, EmojiBitmapFactory.EmojiScale.Large)

        bitmapEmojis.children.toList().forEach {
            if (it !is ImageView) return@forEach
            val bitmap = factory.createEmoji("↙")
            it.setImageBitmap(bitmap)
        }

        bitmapEmojis.onLayout {
            bitmapEmojis.children.toList().forEach { view ->
                if (view !is ImageView) return@forEach
                d("view: ${view.width}x${view.height}")
                view.drawable.let {
                    d("drawable: ${it.intrinsicWidth}x${it.intrinsicHeight}")
                }

            }
        }
    }

}
