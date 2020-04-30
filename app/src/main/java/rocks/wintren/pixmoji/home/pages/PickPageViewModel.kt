package rocks.wintren.pixmoji.home.pages

import androidx.lifecycle.ViewModel

class PickPageViewModel : PageViewModel() {

    val pageName: String = "Pick a Photo"

    fun onPickClick() {
        parent.photoPickedByChild()
    }

}