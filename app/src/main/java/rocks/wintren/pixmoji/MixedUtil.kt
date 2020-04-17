package rocks.wintren.pixmoji

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
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

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toEmoji(): String {
    return String(Character.toChars(this))
}

fun Context.getDrawableFromAssets(fileName: String): Drawable {
    // get input stream
    val imageStream = assets.open(fileName)
    // load image as Drawable
    return Drawable.createFromStream(imageStream, null).also {
        imageStream.close()
    }
}

@ColorInt
fun adjustAlpha(@ColorInt color: Int, alpha: Int): Int {
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
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