package rocks.wintren.pixmoji

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EmojiBitmapFactory(
    private val context: Context,
    private val scale: EmojiScale
) {

    fun createBitmap(emoji: String): Bitmap {
        val emojiTextView = createEmojiTextView(emoji)
        return emojiTextView.toBitmap()
    }

    private fun createEmojiTextView(emoji: String): TextView {
        return TextView(context).apply {
                val sideLength = scale.moxelSize
            layoutParams = ViewGroup.LayoutParams(sideLength, sideLength)
            gravity = Gravity.CENTER
            text = emoji
            setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.textSize)
            includeFontPadding = false
            setTextColor(Color.BLACK)
            // Can't set background here because of trimming
        }
    }

    private fun TextView.toBitmap(): Bitmap {
        val view = this
//        if (width > 0 && height > 0) {
        val sideLength = scale.moxelSize
            view.measure(
                View.MeasureSpec.makeMeasureSpec(sideLength, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(sideLength, View.MeasureSpec.EXACTLY)
            )
//        }
        view.layout(0, 0, sideLength, sideLength)

        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        val background = view.background

        background?.draw(canvas)
        view.draw(canvas)

        return bitmap
    }


    private fun Bitmap.trimSize(): Bitmap {
        val bmp = this
        val imgHeight = bmp.height
        val imgWidth = bmp.width


        //TRIM WIDTH - LEFT
        var startWidth = 0
        for (x in 0 until imgWidth) {
            if (startWidth == 0) {
                for (y in 0 until imgHeight) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        startWidth = x
                        break
                    }
                }
            } else
                break
        }


        //TRIM WIDTH - RIGHT
        var endWidth = 0
        for (x in imgWidth - 1 downTo 0) {
            if (endWidth == 0) {
                for (y in 0 until imgHeight) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        endWidth = x
                        break
                    }
                }
            } else
                break
        }


        //TRIM HEIGHT - TOP
        var startHeight = 0
        for (y in 0 until imgHeight) {
            if (startHeight == 0) {
                for (x in 0 until imgWidth) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        startHeight = y
                        break
                    }
                }
            } else
                break
        }


        //TRIM HEIGHT - BOTTOM
        var endHeight = 0
        for (y in imgHeight - 1 downTo 0) {
            if (endHeight == 0) {
                for (x in 0 until imgWidth) {
                    if (bmp.getPixel(x, y) != Color.TRANSPARENT) {
                        endHeight = y
                        break
                    }
                }
            } else
                break
        }

        // TODO, make square
        // Find cut edges
        // set background
        // trimSize

        return Bitmap.createBitmap(
            bmp,
            startWidth,
            startHeight,
            endWidth - startWidth,
            endHeight - startHeight
        )   // TODO
            .let { Bitmap.createScaledBitmap(it, 25, 25, false) }

    }


    enum class EmojiScale(
        /**
         * The sp used to create the Emoji
         */
        val textSize: Float,
        /**
         * The quadratic size of the EmojiBitmap: Pixels per side.
         */
        val moxelSize: Int
    ) {
        // The values are maybe not perfect but they're pretty and good enough for now!
        Tiny(7f, 22),
        Small(10f, 32),
        Medium(12f, 40),
        Large(16f, 52),
        Huge(24f, 76)
    }

}