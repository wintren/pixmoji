package rocks.wintren.pixmoji

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        quantifySonic()
        emojiSonic()
//        val unicodeEmoji = 0x260E
//        val emojiString = "\uD83D\uDC68\u200D\uD83D\uDC68\u200D\uD83D\uDC67\u200D\uD83D\uDC67"
//        val emojiTextView = TextView(this).apply {
//            text = emojiString
//            textSize = 40f
//            setTextColor(Color.BLACK)
//            // Can't set background here because of trimming
//        }
//        val emojiBitmap = createBitmapFromView(emojiTextView, 52.dp, 52.dp)
//        val trimmed = emojiBitmap.trim()
//
//        val grid = trimmed.grid(100)
//
//        emojiImage.setImageBitmap(grid)


//        EmojiScanner.run(this)

    }

    fun String.emojiToBitmap(context: Context): Bitmap {
        val emojiTextView = TextView(context).apply {
            text = this@emojiToBitmap
            textSize = 40f
            setTextColor(Color.BLACK)
            // Can't set background here because of trimming
        }
        return createBitmapFromView(emojiTextView, 52.dp, 52.dp).trim()
            .let { Bitmap.createScaledBitmap(it, 25, 25, false) }
    }

    private fun quantifySonic() {
        val quantifyColorImage = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888)
        val bitmap = setSonicReturnBitmap()


        for (col in 0 until quantifyColorImage.width) {
            for (row in 0 until quantifyColorImage.height) {
                val pixel = bitmap.get(col, row)
                val moxel = getClosestColor(pixel)
                quantifyColorImage[col, row] = moxel
            }
        }

        originalImage.setImageBitmap(quantifyColorImage)
    }

    private fun emojiSonic() {
        val sonicBitmap = setSonicReturnBitmap()
        val pixelMatrix: Array<Array<Int>> = Array(50) { Array(50) { 0 } }

        for (col in 0 until sonicBitmap.width) {
            for (row in 0 until sonicBitmap.height) {
                pixelMatrix[col][row] = sonicBitmap[col, row]
            }
        }

        val emojiMatrix = pixelMatrix.map { columns ->
            columns.map { pixel ->
                getClosestEmoji(pixel)
            }
        }


        val result = buildBitmap(emojiMatrix)
        emojiImage.setImageBitmap(result)
    }

    private fun buildBitmap(emojiMatrix: List<List<String>>): Bitmap {
        log("emojiMatrix[${emojiMatrix.size}][${emojiMatrix.first().size}]")
        val originalPixelsSide = emojiMatrix.size
        val emojiPixelsSide = originalPixelsSide * 25
        log("side*25=$emojiPixelsSide")

        // TODO refactor with merge technique instead of moving pixels

        return Bitmap.createBitmap(emojiPixelsSide, emojiPixelsSide, Bitmap.Config.ARGB_8888)
            .also { result ->
                for (col in 0 until originalPixelsSide) {
                    for (row in 0 until originalPixelsSide) {
                        log("col: $col")
                        log("row: $row")

                        val emoji = emojiMatrix[col][row]
                        log("Emoji: $emoji")
                        val emojiBitmap = emoji.emojiToBitmap(this)
                        log("emoji bitmap: [${emojiBitmap.width}][${emojiBitmap.height}]")

                        for (emojiCol in 0 until emojiBitmap.width - 1) {
                                val x = col * 25 + emojiCol
                                log("x: $x")
                            for (emojiRow in 0 until emojiBitmap.height - 1) {
                                val y = row * 25 + emojiRow
                                log("y: $y")
                                result[x, y] = emojiBitmap[emojiCol, emojiRow]
                            }
                        }
                    }
                }
            }
    }


    private fun setSonicReturnBitmap(): Bitmap {
        // get input stream
        val imageStream = assets.open("sonic.jpg")
        // load image as Drawable
        val drawable = Drawable.createFromStream(imageStream, null)
        // set image to ImageView
        originalImage.setImageDrawable(drawable)
        imageStream.close()

        val bitmap = drawable.toBitmap()
        return bitmap
    }
}

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

val colors = listOf(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.WHITE, Color.BLACK)
val emojis = mapOf(
    Color.BLUE to "\uD83E\uDD76",
    Color.RED to "\uD83D\uDC8B",
    Color.GREEN to "\uD83E\uDD22",
    Color.YELLOW to "\uD83E\uDD17",
    Color.WHITE to "\uD83E\uDDB7",
    Color.BLACK to "\uD83E\uDD8D"
)

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

fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
    if (width > 0 && height > 0) {
        view.measure(makeMeasureSpec(width, EXACTLY), makeMeasureSpec(height, EXACTLY))
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


fun Bitmap.trim(): Bitmap {
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
    // trim

    return Bitmap.createBitmap(
        bmp,
        startWidth,
        startHeight,
        endWidth - startWidth,
        endHeight - startHeight
    )

}






