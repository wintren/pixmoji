package rocks.wintren.pixmoji

import android.graphics.Bitmap
import android.os.Environment
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream

class MainViewModel : BaseViewModel() {

    private var emojiScale = EmojiBitmapFactory.EmojiScale.Tiny
    private var canvasSize = 3_000

    val originalImage: MutableLiveData<Bitmap> = mutableLiveDataOf()
    val emojiArtwork: MutableLiveData<Bitmap> = mutableLiveDataOf()
    val showLoading: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val showLoadingLabel: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val pickOriginalAvailable: MutableLiveData<Boolean> = mutableLiveDataOf(true)
    val createArtworkAvailable: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val saveArtworkAvailable: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val loadingLabel: MutableLiveData<String> = mutableLiveDataOf()
    val emojiSizeLabel: MutableLiveData<String> = mutableLiveDataOf(emojiScale.name)
    val canvasSizeLabel: MutableLiveData<String> = mutableLiveDataOf("$canvasSize")
    val events: SingleLiveEvent<MainEvent> = SingleLiveEvent()

    fun onEmojiSizeClick() {
        emojiScale = emojiScale.next()
        emojiSizeLabel.value = emojiScale.name
    }

    fun onCanvasSizeClick() {
        val index = canvasSizes.indexOf(canvasSize)
        canvasSize = canvasSizes[(index + 1) % canvasSizes.size]
        canvasSizeLabel.value = "$canvasSize"
    }

    fun onSelectRandomAssetImageClick() {
        val randomFile = files.random()

        val assetDrawable = MojiApp.appContext.getDrawableFromAssets(randomFile)
        pickedImage(assetDrawable.toBitmap())
    }

    fun onPickImageClick() {
        events.value = MainEvent.PickImage
    }

    fun onCreateArtClick() {
        // 10_000 | 01:12:061
        // 20_000 | 05:01:198 Looks really ugly, emojis start re-appearing
        val drawable = originalImage.value ?: return
        val emojiFactory = EmojiBitmapFactory(emojiScale)
        emojiFactory.createArtwork(drawable, emojiScale, canvasSize)
            .observeOn(mainThread)
            .subscribe({ update ->
                when (update) {
                    is EmojiBitmapFactory.CreateArtworkUpdate.Success -> {
                        showLoading.value = false
                        showLoadingLabel.value = false
                        loadingLabel.value = null
                        emojiArtwork.value = update.artwork

                        createArtworkAvailable.value = true
                        saveArtworkAvailable.value = true
                        pickOriginalAvailable.value = true
                    }
                    is EmojiBitmapFactory.CreateArtworkUpdate.InProgress -> {
                        emojiArtwork.value = null
                        events.value = MainEvent.ResetZoom
                        showLoading.value = true
                        showLoadingLabel.value = true

                        pickOriginalAvailable.value = false
                        createArtworkAvailable.value = false
                        saveArtworkAvailable.value = false

                        loadingLabel.value = "${update.percentage}%"
                    }
                }
            }, {
                it.printStackTrace()
            }).clearWithViewModel()

    }

    private var counter = 0
    fun onSaveClick() {
        val bitmap = emojiArtwork.value ?: return
        events.value = MainEvent.CheckPermissions { saveFile(bitmap) }
    }

    private fun saveFile(bitmap: Bitmap) {
        Single.create<String> { emitter ->
            val root = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString()
            val dir = File("$root/${OUTPUT_FOLDER_NAME}")
            dir.mkdirs()

            val name = "EmojiArt"
            var file = File(dir, "$name.png")
            var counter = 0
            while (file.exists()) {
                file = File(dir, "${name}_${counter++}.png")
            }

            try {
                val out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                emitter.onError(e)
            }
            emitter.onSuccess(file.toString())
        }
            .subscribeOn(Schedulers.newThread())
            .doOnSubscribe {
                showLoading.value = true
                createArtworkAvailable.value = false
                saveArtworkAvailable.value = false
                pickOriginalAvailable.value = false
            }
            .observeOn(mainThread)
            .subscribe(
                {
                    showLoading.value = false
                    createArtworkAvailable.value = true
                    saveArtworkAvailable.value = true
                    pickOriginalAvailable.value = true
                    events.value = MainEvent.NotifyMediaScanner(it.toString())
                },
                { it.printStackTrace() }
            )
            .clearWithViewModel()
    }

    fun pickedImage(selectedImage: Bitmap) {
        createArtworkAvailable.value = true
        saveArtworkAvailable.value = false
        emojiArtwork.value = null

        originalImage.value = selectedImage
        events.value = MainEvent.ResetZoom
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
        private const val OUTPUT_FOLDER_NAME = "EmojiArt"
    }

    sealed class MainEvent {
        object ResetZoom : MainEvent()
        object PickImage : MainEvent()
        data class CheckPermissions(val onPermissionGranted: () -> Unit) : MainEvent()

        //        data class SaveEmojiArt(val bitmap: Bitmap) : MainEvent()
        data class NotifyMediaScanner(val filename: String) : MainEvent()
        data class Toast(val message: String) : MainEvent()
    }

}
