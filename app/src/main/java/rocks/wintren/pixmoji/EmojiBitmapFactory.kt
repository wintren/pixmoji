package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.core.graphics.applyCanvas
import androidx.emoji.text.EmojiCompat
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class EmojiBitmapFactory(private val scale: EmojiScale) {

    fun createEmoji(emoji: String): Bitmap {
        val emojiTextView = createEmojiTextView(emoji)
        return emojiTextView.matrixToArtwork()
    }

    sealed class CreateArtworkUpdate {
        data class Success(val artwork: Bitmap) : CreateArtworkUpdate()
        object LoadingEmojis : CreateArtworkUpdate()
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
        originalBitmap: Bitmap,
        emojiScale: EmojiScale,
        imageScale: Int
    ): Observable<CreateArtworkUpdate> {
        val createArtworkObservable = Observable.create<CreateArtworkUpdate> { emitter ->
            val timeBefore = System.currentTimeMillis()
            emitter.onNext(CreateArtworkUpdate.InProgress(0))

            val scaledOriginalBitmap = originalBitmap.scaledBitmap(emojiScale, imageScale)
            val emojiMatrix = scaledOriginalBitmap.toEmojiMatrix()
            val emojiArt = emojiMatrix.matrixToArtwork(emojiScale) {
                emitter.onNext(CreateArtworkUpdate.InProgress(it))
            }

            val timeAfter = System.currentTimeMillis()
            val duration = timeAfter - timeBefore
            val time = SimpleDateFormat("mm:ss:SSS").format(Date(duration))
            w("conversion took, $time")

            emitter.onNext(CreateArtworkUpdate.Success(emojiArt))
        }.subscribeOn(Schedulers.computation())

        return EmojiRepository.ready
            .concatMap { if (it) createArtworkObservable else Observable.just(CreateArtworkUpdate.LoadingEmojis) }
    }

    private fun Bitmap.scaledBitmap(scale: EmojiScale, imageScale: Int): Bitmap {
        val predictedWidth = width * scale.moxelSize
        val predictedHeight = height * scale.moxelSize

        i("Original: ${width}x${height}")
        i("Predicted: ${predictedWidth}x${predictedHeight}")

        return if (predictedWidth > imageScale || predictedHeight > imageScale) {
            val longestPredictedSide = max(predictedHeight, predictedWidth)
            val downScaleFactor: Float = imageScale / longestPredictedSide.toFloat()
            val scaledWidth = (width * downScaleFactor).toInt()
            val scaledHeight = (height * downScaleFactor).toInt()

            val scaledBitmap = Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, false)!!
            i("Scaled Bitmap: ${scaledBitmap.height}x${scaledBitmap.height}")
            scaledBitmap
        } else {
            i("No scale applied: ${width}x${height}")
            this
        }
    }

    private fun Bitmap.toEmojiMatrix(): List<List<Emoji>> {
        setHasAlpha(true)
        val pixelColorMatrix: Array<Array<Int>> =
            Array(width) { Array(height) { Color.TRANSPARENT } }

        for (col in 0 until width) {
            for (row in 0 until height) {
                pixelColorMatrix[col][row] = this.getPixel(col, row)
            }
        }

        return pixelColorMatrix.map { columns ->
            columns.map { pixel ->
                if (pixel != Color.TRANSPARENT) {
                    val noAlphaPixel = Color.valueOf(pixel).let {
                        Color.rgb(it.red(), it.green(), it.blue())
                    }
                    val emoji = try {
                        EmojiRepository.getEmoji(pixel)
                    } catch (e: Exception) {
                        val hexColor = String.format("#%06X", 0xFFFFFF and pixel)
                        throw RuntimeException("Couldn't find emoji for color $hexColor , $pixel")
                    }
                    emoji
                } else Emoji(TRANSPARENT, TRANSPARENT, Color.TRANSPARENT, "X")
            }
        }
    }

    private fun List<List<Emoji>>.matrixToArtwork(
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
                        if (emoji.category != TRANSPARENT) {
                            val emojiBitmap = createEmoji(emoji.emoticon)
                            val x = (col * singleEmojiSide).toFloat()
                            val y = (row * singleEmojiSide).toFloat()
                            val length = emojiBitmap.width
                            val emojiPaint = Paint().apply {
                                val dominantColor = MojiColorUtil.getDominantColor(emojiBitmap)
                                val colorColor = Color.valueOf(dominantColor)
                                val adjustedColor = colorColor.let {
                                    Color.argb(
                                        1f,
                                        it.red(),
                                        it.green(),
                                        it.blue()
                                    )
                                }
                                color = adjustedColor
                            }

                            val whitePaint = Paint().apply { color = Color.WHITE }
                            // Paint white background to not save as transparent.
                            drawRect(x, y, x + length, y + length, whitePaint)
//                            drawCircle(
//                                x + length / 2f,
//                                y + length / 2f,
//                                length * 0.5f,
//                                emojiPaint
//                            )
                            drawBitmap(emojiBitmap, x, y, null)
                        }
                    }
                }
                progressCallback.invoke(100)
            }
    }


    private fun createEmojiTextView(emoji: String): TextView {
        return TextView(MojiApp.appContext).apply {
            val sideLength = scale.moxelSize
            layoutParams = ViewGroup.LayoutParams(sideLength, sideLength)
            gravity = Gravity.CENTER
            text = EmojiCompat.get().process(emoji)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, scale.textSize)
            includeFontPadding = false
            setTextColor(Color.BLACK)
        }
    }

    private fun TextView.matrixToArtwork(): Bitmap {
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
        Huge(24f, 76);

        fun next(): EmojiScale {
            val values = values()
            val index = values.indexOf(this)
            return values[(index + 1) % values.size]
        }
    }

}