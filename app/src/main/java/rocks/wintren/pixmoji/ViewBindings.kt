package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("bitmap")
fun ImageView.bindingSetBitmap(bitmap: Bitmap?) {
    setImageBitmap(bitmap)
}

@BindingAdapter("drawable")
fun ImageView.bindingSetDrawable(drawable: Drawable?) {
    setImageDrawable(drawable)
}

@BindingAdapter("enabled")
fun View.bindingSetEnabled(enabled: Boolean?) {
    isEnabled = enabled ?: return
}

@BindingAdapter("visibleElseGone")
fun View.bindingVisibleElseGone(visible: Boolean?) {
    this.visibility = if (visible == true) VISIBLE else GONE
}
