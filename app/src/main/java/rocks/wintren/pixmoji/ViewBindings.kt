package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.graphics.Color
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

@BindingAdapter("onLongClick")
fun View.bindingOnLongClick(onLongClick: (() -> Unit)?) {
    if (onLongClick == null) setOnLongClickListener(null)
    else setOnLongClickListener {
        onLongClick.invoke()
        true
    }

}

@BindingAdapter("backgroundFromText")
fun View.bindingBackgroundFromText(text: String?) {
    val factory = EmojiBitmapFactory(EmojiBitmapFactory.EmojiScale.Tiny)
    val regex =
        "\\u00a9|\\u00ae|[\\u2000-\\u3300]|\\ud83c[\\ud000-\\udfff]|\\ud83d[\\ud000-\\udfff]|\\ud83e[\\ud000-\\udfff]"
    if(text != null) {
        val bitmap = factory.createEmoji(text)
        val color = MojiColorUtil.getDominantColor(bitmap)
        setBackgroundColor(color)
    } else {
        setBackgroundColor(Color.TRANSPARENT)
    }
}