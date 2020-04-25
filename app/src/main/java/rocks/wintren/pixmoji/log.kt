package rocks.wintren.pixmoji

import android.util.Log

fun d(message: String) = Log.d("PixMoji", message)
fun w(message: String) = Log.w("PixMoji", message)
fun e(message: String) = Log.e("PixMoji", message)
fun e(message: String, throwable: Throwable) = Log.e("PixMoji", message, throwable)
fun i(message: String) = Log.i("PixMoji", message)
fun v(message: String) = Log.v("PixMoji", message)