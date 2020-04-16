package rocks.wintren.pixmoji

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import androidx.annotation.IntRange

fun View.show() {
    visibility = VISIBLE
}

fun View.conceal() {
    visibility = INVISIBLE
}

fun View.hide() {
    visibility = GONE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun View.showOrHide(show: Boolean) = if (show) show() else hide()

fun View.onLayout(cb: () -> Unit) {
    if (this.viewTreeObserver.isAlive) {
        this.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    cb()
                    this@onLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
    }
}

// Spinners

fun Spinner.onItemSelected(cb: (index: Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) = Unit

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) = cb(position)
    }
}

// ScrollView

fun ScrollView.onScroll(cb: (y: Int) -> Unit) =
    viewTreeObserver.addOnScrollChangedListener { cb(scrollY) }

// EditText

fun EditText.onTextChanged(
    @IntRange(from = 0, to = 10000) debounce: Int = 0,
    cb: (text: String) -> Unit
) {
    addTextChangedListener(object : TextWatcher {
        val callbackRunner = Runnable {
            cb(text.trim().toString())
        }

        override fun afterTextChanged(s: Editable?) = Unit

        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) = Unit

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            removeCallbacks(callbackRunner)
            if (debounce == 0) {
                callbackRunner.run()
            } else {
                postDelayed(callbackRunner, debounce.toLong())
            }
        }
    })
}