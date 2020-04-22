package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.get
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.pow

object EmojiColor {

    private fun Bitmap.getPixels(): IntArray {
        val intArray = IntArray(width * height) { Color.TRANSPARENT }
        getPixels(intArray, 0, width, 0, 0, width, height)
        return intArray
    }

    // TODO, Not working!

    /**
     * JavaScript found online
     */
    fun emojiColor(bitmap: Bitmap): Int {
        var totalPixels = 0
        var red = 0f
        var green = 0f
        var blue = 0f
        var alpha = 0f

        bitmap.getPixels()
            .map { Color.valueOf(it) }
            .forEach {
                if (it.alpha() > 50) {
                    totalPixels += 1
                    red += it.red()
                    green += it.green()
                    blue += it.blue()
                    alpha += it.alpha()
                }
            }
        val averageRed = red / totalPixels
        val averageGreen = green / totalPixels
        val averageBlue = blue / totalPixels

        return Color.rgb(averageRed, averageGreen, averageBlue)

    }
//    const avgColor = useMemo(
//    () => {
//        let totalPixels = 0;
//        const colors = {
//                red: 0,
//                green: 0,
//                blue: 0,
//                alpha: 0
//        };
//        const canvas = document.createElement("canvas");
//        const ctx = canvas.getContext("2d");
//        ctx.font = "30px Arial";
//        ctx.fillText(emoji, 0, 28);
//        const { data: imageData } = ctx.getImageData(0, 0, 30, 30);
//        for (let i = 0; i < imageData.length; i += 4) {
//            let [r, g, b, a] = imageData.slice(i, i + 4);
//            if (a > 50) {
//                totalPixels += 1;
//                colors.red += r;
//                colors.green += g;
//                colors.blue += b;
//                colors.alpha += a;
//            }
//        }
//        const r = getAvgHex(colors.red, totalPixels);
//        const g = getAvgHex(colors.green, totalPixels);
//        const b = getAvgHex(colors.blue, totalPixels);
//
//        return "#" + r + g + b;
//    },
//    [emoji]
//    );


    fun getDominantColor(bitmap: Bitmap): Int {
        val newBitmap = Bitmap.createScaledBitmap(bitmap, 20, 20, true)
        val color = newBitmap.getAverageRGB()
        newBitmap.recycle()
        return color
    }

    fun Bitmap.getAverageRGB(): Int {
        var red: Float = 0f
        var green: Float = 0f
        var blue: Float = 0f
        var num = 0
        /* Iterate through a bounding box in which the circle lies */
        for (x in 0 until width) {
            for (y in 0 until height) {
                /* If the pixel is outside the canvas, skip it */
                if (x < 0 || x >= width || y < 0 || y >= height) continue

                val pixel = this[x, y]
                if (pixel == Color.TRANSPARENT) {
                    continue
                } else {
                    w("color $pixel")
                }
                /* Get the color from the image, add to a running sum */
                val pixelColor: Color = Color.valueOf(pixel)

                /* Sum the squares of components instead */
                red += pixelColor.red().pow(2)
                green += pixelColor.green().pow(2)
                blue += pixelColor.blue().pow(2)
                num++
            }
        }
        /* Return the mean of the R, G, and B components */
        return Color.rgb(red / num, green / num, blue / num)
    }

    /*
 * Averages the pixels in a given image (img) within a circular
 * region centered at (x, y) with a radius of 'radius' pixels.
 * This function uses the "simple" approach to average RGB
 * colors which simply returns the mean of the red, green, and
 * blue components.
 */
    fun Bitmap.getAverageRGBCircle(x: Int, y: Int, radius: Int): Int {
        var red: Float = 0f
        var green: Float = 0f
        var blue: Float = 0f
        var num = 0
        /* Iterate through a bounding box in which the circle lies */
        for (i in x - radius until x + radius) {
            for (j in y - radius until y + radius) {
                /* If the pixel is outside the canvas, skip it */
                if (i < 0 || i >= width || j < 0 || j >= height) continue

                /* If the pixel is outside the circle, skip it */
                if (distance(x, y, i, j) > red) continue
                /* Get the color from the image, add to a running sum */
                val pixel = this[i, j]
                if (pixel == Color.TRANSPARENT) {
                    continue
                } else {
                    w("color $pixel")
                }
                val pixelColor: Color = Color.valueOf(pixel)

                /* Sum the squares of components instead */
                red += pixelColor.red().pow(2)
                green += pixelColor.green().pow(2)
                blue += pixelColor.blue().pow(2)
                num++
            }
        }
        /* Return the mean of the R, G, and B components */
        return Color.rgb(red / num, green / num, blue / num)
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Double {
        val ac: Double = abs(y2.toDouble() - y1.toDouble())
        val cb = abs(x2.toDouble() - x1.toDouble())
        return hypot(ac, cb)
    }
}