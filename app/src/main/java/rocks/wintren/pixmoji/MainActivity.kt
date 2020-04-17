package rocks.wintren.pixmoji

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.max
import kotlin.math.sqrt

val colors = listOf(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.WHITE, Color.BLACK)
val emojis = mapOf(
    Color.BLUE to "\uD83E\uDD76",
    Color.RED to "\uD83D\uDC8B",
    Color.GREEN to "\uD83E\uDD22",
    Color.YELLOW to "\uD83E\uDD17",
    Color.WHITE to "\uD83E\uDDB7",
    Color.BLACK to "\uD83E\uDD8D"
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // activity_main

        val file = "link.jpg"
        val emojiScale = EmojiBitmapFactory.EmojiScale.Tiny
        val imageScale = 6000
        val original = getDrawableFromAssets(file)
        originalImage.setImageDrawable(original)


        val drawable = getDrawableFromAssets(file)
        val originalBitmap = drawable.mutate().scaledBitmap(emojiScale, imageScale)
        log("Creating EmojiMatrix...")
        val emojiMatrix = originalBitmap.toEmojiMatrix()
        log("Creating Emoji Art...")
        val emojiArt = buildEmojiArt(emojiMatrix, emojiScale)
        log("Done, Presenting Art!")
        emojiImage.setImageBitmap(emojiArt)


        // SUPER TODO !!!! in BuildBitmap
        // TODO refactor with merge technique instead of moving pixels

        // activity_main2
//        val factory = EmojiBitmapFactory(this, EmojiBitmapFactory.EmojiScale.Large)
//
//        bitmapEmojis.children.toList().forEach {
//            if (it !is ImageView) return@forEach
//            val bitmap = factory.createBitmap("↙")
//            it.setImageBitmap(bitmap)
//        }
//
//        bitmapEmojis.onLayout {
//            bitmapEmojis.children.toList().forEach { view ->
//                if (view !is ImageView) return@forEach
//                log("view: ${view.width}x${view.height}")
//                view.drawable.let {
//                    log("drawable: ${it.intrinsicWidth}x${it.intrinsicHeight}")
//                }
//
//            }
//        }


    }

    private fun getDrawableFromAssets(fileName: String): Drawable {
        // get input stream
        val imageStream = assets.open(fileName)
        // load image as Drawable
        return Drawable.createFromStream(imageStream, null).also {
            imageStream.close()
        }
    }

    private fun Drawable.scaledBitmap(
        scale: EmojiBitmapFactory.EmojiScale,
        imageScale: Int
    ): Bitmap {

        val predictedWidth = intrinsicWidth * scale.moxelSize
        val predictedHeight = intrinsicHeight * scale.moxelSize

        log("Original: ${intrinsicWidth}x${intrinsicHeight}")
        log("Predicted: ${predictedWidth}x${predictedHeight}")

        val (width: Int, height: Int) = if (predictedWidth > imageScale || predictedHeight > imageScale) {
            val longestPredictedSide = max(predictedHeight, predictedWidth)
            val downScaleFactor: Float = imageScale / longestPredictedSide.toFloat()
            val scaledWidth = intrinsicWidth * downScaleFactor
            val scaledHeight = intrinsicHeight * downScaleFactor
            scaledWidth.toInt() to scaledHeight.toInt()
        } else {
            intrinsicWidth to intrinsicHeight
        }
        log("Scaled Bitmap: ${width}x${height}")
        return this.toBitmap(width, height)

    }

//    override fun onResume() {
//        super.onResume()
//
//        textEmojis.onLayout {
//            val imageViews = bitmapEmojis.children.toList().map { it as? ImageView }.filterNotNull()
//            textEmojis.children.forEachIndexed { index, view ->
//                log("TextView size: ${view.width}x${view.height}")
//                val textView = view as? TextView ?: return@forEachIndexed
////                val emoticon = textView.text
//                val bitmap = createBitmapFromView(textView, textView.width, textView.height)
//                val imageView = imageViews[index]
//                imageView.setImageBitmap(bitmap)
//                log("Bitmap size: ${bitmap.width}x${bitmap.height}")
//            }
//        }
//    }

    fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
        if (width > 0 && height > 0) {
            view.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            )
        }
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

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


    private fun emojiSonic() {
//        val sonicBitmap: Bitmap = TODO() //setSonicReturnBitmap()
//        val emojiMatrix = emojiMatrix(sonicBitmap)


//        val result = buildEmojiArt(emojiMatrix, Small)

    }

    private fun Bitmap.toEmojiMatrix(): List<List<String>> {
        val pixelColorMatrix: Array<Array<Int>> =
            Array(width) { Array(height) { 0 } }

        for (col in 0 until width) {
            for (row in 0 until height) {
                pixelColorMatrix[col][row] = this[col, row]
            }
        }

        return pixelColorMatrix.map { columns -> columns.map { pixel -> getClosestEmoji(pixel) } }
    }

    private fun buildEmojiArt(
        emojiMatrix: List<List<String>>,
        scale: EmojiBitmapFactory.EmojiScale
    ): Bitmap {

        val emojiColumns = emojiMatrix.size
        val emojiRows = emojiMatrix.first().size
        val singleEmojiSide = scale.moxelSize
        val resultArtworkWidth = emojiColumns * singleEmojiSide
        val resultArtworkHeight = emojiRows * singleEmojiSide

        log("Emoji size: ${singleEmojiSide}x${singleEmojiSide}")
        log("Original Image: ${emojiColumns}x${emojiRows}")
        log("Result Artwork Side: ${resultArtworkWidth}x${resultArtworkHeight}")

        // We might be drawing and reading wrong x2 so it ends up correct, but it evens out :P

        // SUPER TODO !!!!
        // TODO refactor with merge technique instead of moving pixels
        // Move into factory

        return Bitmap.createBitmap(resultArtworkWidth, resultArtworkHeight, Bitmap.Config.ARGB_8888)
            .also { result ->
                val emojiFactory = EmojiBitmapFactory(this, scale)
                var currentProgress = 0
                for (col in 0 until emojiColumns) {
                    val percentDone = (col / emojiColumns.toFloat() * 100).toInt()
                    if (percentDone != 100 && percentDone != currentProgress) {
                        currentProgress = percentDone
                        log("$currentProgress%")
                    }
                    for (row in 0 until emojiRows) {

                        val emoji = emojiMatrix[col][row]
//                        log("Emoji: $emoji")
                        val emojiBitmap = emojiFactory.createBitmap(emoji)
                        for (emojiCol in 0 until emojiBitmap.width) {
                            val x = col * singleEmojiSide + emojiCol
                            for (emojiRow in 0 until emojiBitmap.height) {
                                val y = row * singleEmojiSide + emojiRow
                                result[x, y] = emojiBitmap[emojiCol, emojiRow]
                            }
                        }
                    }
                }
                log("100%")
            }
    }

}

// Mario
// Emoji size: 76x76
// Original Image: 300x256
// Result Artwork Side: 22800x19456

fun mergeBitmap(fr: Bitmap, sc: Bitmap): Bitmap {

    val comboBitmap: Bitmap

    val width: Int = fr.width + sc.width
    val height: Int = fr.height

    comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val comboImage = Canvas(comboBitmap)


    comboImage.drawBitmap(fr, 0f, 0f, null)
    comboImage.drawBitmap(sc, fr.width.toFloat(), 0f, null)
    return comboBitmap

}

fun Bitmap.grid(n: Int): Bitmap {
    return Bitmap.createBitmap(width * n, height * n, Bitmap.Config.ARGB_8888)
        .also { result ->
            val canvas = Canvas(result)
            for (col in 0 until n) {
                for (row in 0 until n) {
                    val left = col * width
                    val top = row * height
                    canvas.drawBitmap(this, left.toFloat(), top.toFloat(), null)
                }
            }
        }
}

fun log(message: String) = Log.d("PixMoji", message)

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun getEmoji(unicode: Int): String {
    return String(Character.toChars(unicode))
}

fun getClosestColor(pixelColor: Int): Int {
    return colors
        .map { color -> color to colorDistance(pixelColor, color) }
        .sortedBy { it.second }
        .map { it.first }
        .first()
}

fun getClosestEmoji(pixelColor: Int): String {
    return colors
        .map { color -> color to colorDistance(pixelColor, color) }
        .sortedBy { it.second }
        .map { it.first }
        .first().let { emojis.getValue(it) }
}

fun colorDistance(colorInt1: Int, colorInt2: Int): Double {
    val c1 = Color.valueOf(colorInt1)
    val c2 = Color.valueOf(colorInt2)
    val red1 = c1.red()
    val red2 = c2.red()
    val rmean = (red1 + red2).toInt() shr 1
    val r = red1 - red2
    val g = c1.green() - c2.green()
    val b = c1.blue() - c2.blue()
    val redValues = ((512 + rmean) * r * r).toInt() shr 8
    val greenValues = 4 * g * g
    val blueValues = ((767 - rmean) * b * b).toInt() shr 8
    return sqrt((redValues + greenValues + blueValues).toDouble())
}








