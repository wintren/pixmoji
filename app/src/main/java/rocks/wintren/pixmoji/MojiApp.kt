package rocks.wintren.pixmoji

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig

class MojiApp : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this

//        val fontRequest = androidx.core.provider.FontRequest(
//            "com.google.android.gms.fonts",
//            "com.google.android.gms",
//            "Noto Color Emoji Compat",
//            android.R.array.com_google_android_gms_fonts_certs
//        )
        val config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)

//        val config = BundledEmojiCompatConfig(this)
//        EmojiCompat.init(config)

//        val fontRequest = FontRequest(
//            "com.example.fontprovider",
//            "com.example",
//            "emoji compat Font Query",
//            EmojiCompat.CERTIFICATES
//        )
//        val config = FontRequestEmojiCompatConfig(this, fontRequest)
//        EmojiCompat.init(config)

    }


    companion object {
    lateinit var appContext: Application

    }
}