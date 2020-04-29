package rocks.wintren.pixmoji.utils

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max

object PageTransformers {
    val DepthPage = DepthPageTransformer()
    val ZoomOutSlide = ZoomOutSlideTransformer()
}

class DepthPageTransformer : ViewPager2.PageTransformer {

    private companion object {
        const val MIN_SCALE = 0.75f
    }

    override fun transformPage(view: View, position: Float) {
        if (position <= 0f) {
            view.translationX = 0f
            view.scaleX = 1f
            view.scaleY = 1f
        } else if (position <= 1f) {
            val scaleFactor =
                MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
            view.alpha = 1 - position
            view.pivotY = 0.5f * view.height
            view.translationX = view.width * -position
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor
        }
    }
}


class ZoomOutSlideTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        if (position >= -1 || position <= 1) {
            // Modify the default slide transition to shrink the page as well
            val height = view.height.toFloat()
            val scaleFactor = max(
                MIN_SCALE,
                1 - abs(position)
            )
            val vertMargin = height * (1 - scaleFactor) / 2
            val horzMargin = view.width * (1 - scaleFactor) / 2

            // Center vertically
            view.pivotY = 0.5f * height
            if (position < 0) {
                view.translationX = horzMargin - vertMargin / 2
            } else {
                view.translationX = -horzMargin + vertMargin / 2
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.scaleX = scaleFactor
            view.scaleY = scaleFactor

            // Fade the page relative to its size.
            view.alpha = MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
        }
    }

    private companion object {
        const val MIN_SCALE = 0.85f
        const val MIN_ALPHA = 0.5f
    }
}
