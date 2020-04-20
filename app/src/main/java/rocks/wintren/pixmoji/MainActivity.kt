package rocks.wintren.pixmoji

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import rocks.wintren.pixmoji.MainViewModel.MainEvent.*
import rocks.wintren.pixmoji.databinding.ActivityMainBinding
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.events.observe(this, ::onEvent)
    }

    private fun onEvent(event: MainViewModel.MainEvent): Unit = when (event) {
        ResetZoom -> zoomLayout.resetZoom()
        PickImage -> launchPhotoPicker()
        is CheckPermissions -> handlePermissions(event.onPermissionGranted)
        is MainViewModel.MainEvent.Toast -> Toast
                .makeText(this, event.message, Toast.LENGTH_SHORT)
                .show()

        is NotifyMediaScanner -> {
            notifyMediaScanner(event.filename)
        }
    }

    private fun notifyMediaScanner(fileName: String) {
        MediaScannerConnection.scanFile(this, arrayOf(fileName), null) { path, uri ->
            Log.i("ExternalStorage", "Scanned $path:")
            Log.i("ExternalStorage", "-> uri=$uri")
        }
    }

    private fun handlePermissions(permissionGrantedCallback: () -> Unit) {
        // Here, thisActivity is the current activity
        val writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionStatus = ContextCompat.checkSelfPermission(
            this@MainActivity,
            writeExternalStorage
        )
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    writeExternalStorage
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast
                    .makeText(this, "You need to grant that permission, dude...", Toast.LENGTH_LONG)
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                onPermissionGranted = permissionGrantedCallback
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(writeExternalStorage),
                    PERMISSION_REQUEST
                )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            permissionGrantedCallback.invoke()
        }
    }

    var onPermissionGranted: (() -> Unit)? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onPermissionGranted?.invoke()
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                onPermissionGranted = null
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun launchPhotoPicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_PHOTO_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        try {
            val imageUri: Uri = data?.data ?: return
            val imageStream = contentResolver.openInputStream(imageUri)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            viewModel.pickedImage(selectedImage)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun measureEmojis() {
        val factory = EmojiBitmapFactory(EmojiBitmapFactory.EmojiScale.Large)

        bitmapEmojis.children.toList().forEach {
            if (it !is ImageView) return@forEach
            val bitmap = factory.createEmoji("↙")
            it.setImageBitmap(bitmap)
        }

        bitmapEmojis.onLayout {
            bitmapEmojis.children.toList().forEach { view ->
                if (view !is ImageView) return@forEach
                d("view: ${view.width}x${view.height}")
                view.drawable.let {
                    d("drawable: ${it.intrinsicWidth}x${it.intrinsicHeight}")
                }

            }
        }
    }

    companion object {
        private const val RESULT_PHOTO_PICKER = 20
        private const val PERMISSION_REQUEST = 5
    }

}
