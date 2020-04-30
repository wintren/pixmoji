package rocks.wintren.pixmoji.home

import androidx.lifecycle.MutableLiveData
import rocks.wintren.pixmoji.BaseViewModel
import rocks.wintren.pixmoji.mutableLiveDataOf

class HomeActivityViewModel: BaseViewModel() {

    val pickButtonVisible: MutableLiveData<Boolean> = mutableLiveDataOf(true)
    val pickButtonText: MutableLiveData<String> = mutableLiveDataOf("Pick a photo")

    val optionsButtonVisible: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val optionsButtonText: MutableLiveData<String> = mutableLiveDataOf("Art options")

    val createButtonVisible: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val createButtonText: MutableLiveData<String> = mutableLiveDataOf("Create Artwork")

    val artButtonVisible: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val artButtonText: MutableLiveData<String> = mutableLiveDataOf("Display Artwork")


    fun onPickButtonClick() {

    }

    fun onOptionsButtonClick() {

    }

    fun onCreateButtonClick() {

    }

    fun onArtButtonClick() {

    }

}