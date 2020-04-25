package rocks.wintren.pixmoji

import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat

class MojiApp : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this
        initEmojis()
    }

    private fun initEmojis() {
        val config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)
        EmojiCompat.get().registerInitCallback(object : EmojiCompat.InitCallback() {
            override fun onInitialized() {
                EmojiScanner.importEmojis()
            }

            override fun onFailed(throwable: Throwable?) {
                throw throwable!!
            }
        })
    }


    companion object {
        lateinit var appContext: Application

    }
}