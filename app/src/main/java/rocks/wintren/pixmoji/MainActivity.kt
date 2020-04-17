package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val file = "link.jpg"
        val emojiScale = EmojiBitmapFactory.EmojiScale.Tiny
        val imageScale = 6000

        runEmojiArt(file, emojiScale, imageScale)

//        measureEmojis()
    }

    private fun runEmojiArt(
        file: String,
        emojiScale: EmojiBitmapFactory.EmojiScale,
        imageScale: Int
    ) {
        originalImage.setImageDrawable(getDrawableFromAssets(file))
        val drawable = getDrawableFromAssets(file)
        val emojiFactory = EmojiBitmapFactory(this, emojiScale)
        val artwork: Bitmap = emojiFactory.createArtwork(drawable, emojiScale, imageScale)
        emojiImage.setImageBitmap(artwork)
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
