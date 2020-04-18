package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData

class MainViewModel : BaseViewModel() {

    private var emojiScale = EmojiBitmapFactory.EmojiScale.Tiny
    private var canvasSize = 3_000

    val originalImage: MutableLiveData<Drawable> = mutableLiveDataOf()
    val emojiArtwork: MutableLiveData<Bitmap> = mutableLiveDataOf()
    val showLoading: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val pickOriginalAvailable: MutableLiveData<Boolean> = mutableLiveDataOf(true)
    val createArtworkAvailable: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val saveArtworkAvailable: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val loadingLabel: MutableLiveData<String> = mutableLiveDataOf()
    val emojiSizeLabel: MutableLiveData<String> = mutableLiveDataOf(emojiScale.name)
    val canvasSizeLabel: MutableLiveData<String> = mutableLiveDataOf("$canvasSize")
    val resetZoom: SingleLiveEvent<Unit> = SingleLiveEvent()


    fun onEmojiSizeClick() {
        emojiScale = emojiScale.next()
        emojiSizeLabel.value = emojiScale.name
    }

    fun onCanvasSizeClick() {
        val index = canvasSizes.indexOf(canvasSize)
        canvasSize = canvasSizes[(index + 1) % canvasSizes.size]
        canvasSizeLabel.value = "$canvasSize"
    }

    fun onPickImageClick() {
        val randomFile = files.random()

        originalImage.value = PixMojiApp.appContext.getDrawableFromAssets(randomFile)
        createArtworkAvailable.value = true
        saveArtworkAvailable.value = false
        emojiArtwork.value = null

        resetZoom.call()
    }

    fun onCreateArtClick() {
        // 10_000 | 01:12:061
        // 20_000 | 05:01:198 Looks really ugly, emojis start re-appearing
        val drawable = originalImage.value ?: return
        val emojiFactory = EmojiBitmapFactory(emojiScale)
        emojiFactory.createArtwork(drawable, emojiScale, canvasSize)
            .observeOn(mainScheduler)
            .subscribe { update ->
                when (update) {
                    is EmojiBitmapFactory.CreateArtworkUpdate.Success -> {
                        showLoading.value = false
                        loadingLabel.value = null
                        emojiArtwork.value = update.artwork

                        createArtworkAvailable.value = true
                        saveArtworkAvailable.value = true
                        pickOriginalAvailable.value = true
                    }
                    is EmojiBitmapFactory.CreateArtworkUpdate.InProgress -> {
                        emojiArtwork.value = null
                        resetZoom.call()
                        showLoading.value = true

                        pickOriginalAvailable.value = false
                        createArtworkAvailable.value = false
                        saveArtworkAvailable.value = false

                        loadingLabel.value = "${update.percentage}%"
                    }
                }
            }.clearWithViewModel()

    }

    fun onSaveClick() {
        // ToDo
    }

    private companion object {
        private val canvasSizes = listOf(1000, 3000, 5000, 9999)
        private val files = listOf(
            "landscape.jpeg",
            "link.jpg",
            "mario.png",
            "mario_cart.jpg",
            "milo.png",
            "pikachu.jpg",
            "sonic.jpg",
            "sonic2.png"
        )
    }

}
