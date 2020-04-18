package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData

class MainViewModel : BaseViewModel() {

    val originalImage: MutableLiveData<Drawable> = mutableLiveDataOf()
    val emojiArtwork: MutableLiveData<Bitmap> = mutableLiveDataOf()
    val showLoading: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val loadingLabel: MutableLiveData<String> = mutableLiveDataOf()

    fun onPickImageClick() {

    }

    fun onCreateArtClick() {

        val file = "sonic2.png"
        val emojiScale = EmojiBitmapFactory.EmojiScale.Tiny
        val imageScale = 6_000
        // 10_000 | 01:12:061
        // 20_000 | 05:01:198 Looks really ugly, emojis start re-appearing
        createArt(file, emojiScale, imageScale)
    }

    fun onSaveClick() {

    }

    private fun createArt(
        file: String,
        emojiScale: EmojiBitmapFactory.EmojiScale,
        imageScale: Int
    ) {
        val context = PixMojiApp.appContext
        originalImage.value = context.getDrawableFromAssets(file)
        val drawable = context.getDrawableFromAssets(file)
        val emojiFactory = EmojiBitmapFactory(emojiScale)
        emojiFactory.createArtwork(drawable, emojiScale, imageScale)
            .observeOn(mainScheduler)
            .subscribe { update ->
                when (update) {
                    is EmojiBitmapFactory.CreateArtworkUpdate.Success -> {
                        showLoading.value = false
                        loadingLabel.value = null
                        emojiArtwork.value = update.artwork
                    }
                    is EmojiBitmapFactory.CreateArtworkUpdate.InProgress -> {
                        showLoading.value = true
                        loadingLabel.value = "${update.percentage}%"
                    }
                }
            }.clearWithViewModel()

    }

}
