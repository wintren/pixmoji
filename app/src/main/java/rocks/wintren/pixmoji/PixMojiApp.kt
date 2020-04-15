package rocks.wintren.pixmoji

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig

class PixMojiApp : Application() {

    override fun onCreate() {
        super.onCreate()


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

}