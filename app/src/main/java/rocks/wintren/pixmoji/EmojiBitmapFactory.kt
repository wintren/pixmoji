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
import androidx.core.graphics.drawable.toBitmap
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
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

    fun createArtwork(
        drawable: Drawable,
        emojiScale: EmojiScale,
        imageScale: Int
    ): Observable<CreateArtworkUpdate> {
        return Observable.create<CreateArtworkUpdate> { emitter ->
            emitter.onNext(CreateArtworkUpdate.InProgress(0))
            val originalBitmap = drawable.scaledBitmap(emojiScale, imageScale)
            val emojiMatrix = originalBitmap.toEmojiMatrix()
            val emojiArt = emojiMatrix.toBitmap(emojiScale) {
                emitter.onNext(CreateArtworkUpdate.InProgress(it))
            }
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

        val emojiColumns = size
        val emojiRows = first().size
        val singleEmojiSide = scale.moxelSize
        val resultArtworkWidth = emojiColumns * singleEmojiSide
        val resultArtworkHeight = emojiRows * singleEmojiSide

        i("Emoji size: ${singleEmojiSide}x${singleEmojiSide}")
        i("Original Image: ${emojiColumns}x${emojiRows}")
        i("Result Artwork Side: ${resultArtworkWidth}x${resultArtworkHeight}")

        // SUPER TODO !!!!
        // TODO refactor with merge technique instead of moving pixels

        return Bitmap.createBitmap(resultArtworkWidth, resultArtworkHeight, Bitmap.Config.ARGB_8888)
            .also { result ->
                var currentProgress = 0
                for (col in 0 until emojiColumns) {
                    val percentDone = (col / emojiColumns.toFloat() * 100).toInt()
                    if (percentDone != 100 && percentDone != currentProgress) {
                        currentProgress = percentDone
                        progressCallback.invoke(percentDone)
                    }
                    for (row in 0 until emojiRows) {

                        val emoji = this[col][row]
                        val emojiBitmap = createEmoji(emoji)
                        for (emojiCol in 0 until emojiBitmap.width) {
                            val x = col * singleEmojiSide + emojiCol
                            for (emojiRow in 0 until emojiBitmap.height) {
                                val y = row * singleEmojiSide + emojiRow
                                val pixel = emojiBitmap.getPixel(emojiCol, emojiRow)
                                result.setPixel(x, y, pixel)
                            }
                        }
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
            // Can't set background here because of trimming
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