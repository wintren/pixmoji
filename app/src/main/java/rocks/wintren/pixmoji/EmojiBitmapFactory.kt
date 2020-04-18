package rocks.wintren.pixmoji

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.toBitmap
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class EmojiBitmapFactory(
    private val context: Context,
    private val scale: EmojiScale
) {

    fun createEmoji(emoji: String): Bitmap {
        val emojiTextView = createEmojiTextView(emoji)
        return emojiTextView.toBitmap()
    }

    sealed class CreateArtworkUpdate {
        data class Success(val artwork: Bitmap) : CreateArtworkUpdate()
        data class InProgress(
            @IntRange(
                from = 0,
                to = 100
            ) val percentage: Int
        ) : CreateArtworkUpdate()
    }

    // SUPER TODO !!!! in BuildBitmap
    // TODO refactor with merge technique instead of moving pixels

    /**
     * BENCHMARK
     * Link.jpg, 1500x1500, Tiny, max 6000x6000. Took 01:00:178 with pixel-shifting
     * Link.jpg, 1500x1500, Tiny, max 6000x6000. Took 01:01:730 with pixel-shifting (pale color fix)
     * Link.jpg, 1500x1500, Tiny, max 6000x6000. Took 00:29:269 with pixel-shifting
     * Link.jpg, 1500x1500, Tiny, max 6000x6000. Took 00:35:059 with pixel-shifting (pale color fix)
     */

    fun createArtwork(
        drawable: Drawable,
        emojiScale: EmojiScale,
        imageScale: Int
    ): Observable<CreateArtworkUpdate> {
        return Observable.create<CreateArtworkUpdate> { emitter ->
            val timeBefore = System.currentTimeMillis()
            emitter.onNext(CreateArtworkUpdate.InProgress(0))

            val originalBitmap = drawable.scaledBitmap(emojiScale, imageScale)
            val emojiMatrix = originalBitmap.toEmojiMatrix()
            val emojiArt = emojiMatrix.toBitmap(emojiScale) {
                emitter.onNext(CreateArtworkUpdate.InProgress(it))
            }

            val timeAfter = System.currentTimeMillis()
            val duration = timeAfter - timeBefore
            val time = SimpleDateFormat("mm:ss:SSS").format(Date(duration))
            w("conversion took, $time")

            emitter.onNext(CreateArtworkUpdate.Success(emojiArt))
        }.subscribeOn(Schedulers.computation())
    }

    private fun Drawable.scaledBitmap(
        scale: EmojiScale,
        imageScale: Int
    ): Bitmap {

        val predictedWidth = intrinsicWidth * scale.moxelSize
        val predictedHeight = intrinsicHeight * scale.moxelSize

        i("Original: ${intrinsicWidth}x${intrinsicHeight}")
        i("Predicted: ${predictedWidth}x${predictedHeight}")

        val (width: Int, height: Int) = if (predictedWidth > imageScale || predictedHeight > imageScale) {
            val longestPredictedSide = max(predictedHeight, predictedWidth)
            val downScaleFactor: Float = imageScale / longestPredictedSide.toFloat()
            val scaledWidth = intrinsicWidth * downScaleFactor
            val scaledHeight = intrinsicHeight * downScaleFactor
            scaledWidth.toInt() to scaledHeight.toInt()
        } else {
            intrinsicWidth to intrinsicHeight
        }
        i("Scaled Bitmap: ${width}x${height}")
        return this.toBitmap(width, height)
    }

    private fun Bitmap.toEmojiMatrix(): List<List<String>> {
        val pixelColorMatrix: Array<Array<Int>> =
            Array(width) { Array(height) { 0 } }

        for (col in 0 until width) {
            for (row in 0 until height) {
                pixelColorMatrix[col][row] = this.getPixel(col, row)
            }
        }

        return pixelColorMatrix.map { columns -> columns.map { pixel -> getClosestEmoji(pixel) } }
    }

    private fun List<List<String>>.toBitmap(
        scale: EmojiScale,
        progressCallback: (percentDone: Int) -> Unit
    ): Bitmap {
        val emojiMatrix = this
        val emojiColumns = emojiMatrix.size
        val emojiRows = emojiMatrix.first().size
        val singleEmojiSide = scale.moxelSize
        val resultArtworkWidth = emojiColumns * singleEmojiSide
        val resultArtworkHeight = emojiRows * singleEmojiSide

        i("Emoji size: ${singleEmojiSide}x${singleEmojiSide}")
        i("Original Image: ${emojiColumns}x${emojiRows}")
        i("Result Artwork Side: ${resultArtworkWidth}x${resultArtworkHeight}")

        // SUPER TODO !!!!
        // TODO refactor with merge technique instead of moving pixels

        return Bitmap.createBitmap(resultArtworkWidth, resultArtworkHeight, Bitmap.Config.ARGB_8888)
            .applyCanvas {
                var currentProgress = 0
                for (col in 0 until emojiColumns) {
                    val percentDone = (col / emojiColumns.toFloat() * 100).toInt()
                    if (percentDone != 100 && percentDone != currentProgress) {
                        currentProgress = percentDone
                        progressCallback.invoke(percentDone)
                    }
                    for (row in 0 until emojiRows) {
                        val emoji = emojiMatrix[col][row]
                        val emojiBitmap = createEmoji(emoji)
                        val x = col * singleEmojiSide + row
                        val y = row * singleEmojiSide + col
                        drawBitmap(emojiBitmap, x.toFloat(), y.toFloat(), null)
                    }
                }
                progressCallback.invoke(100)
            }
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

            // Basic and hacky way to get background color, works though
//            val emojiColor = emojis.filterValues { it == emoji }.keys.first()
//            setBackgroundColor(adjustAlpha(emojiColor, 100))
        }
    }

    private fun TextView.toBitmap(): Bitmap {
        val view = this
        val sideLength = scale.moxelSize
        view.measure(
            View.MeasureSpec.makeMeasureSpec(sideLength, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(sideLength, View.MeasureSpec.EXACTLY)
        )
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