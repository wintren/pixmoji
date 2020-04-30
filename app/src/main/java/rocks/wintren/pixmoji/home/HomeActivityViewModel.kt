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

    val displayButtonVisible: MutableLiveData<Boolean> = mutableLiveDataOf(false)
    val displayButtonText: MutableLiveData<String> = mutableLiveDataOf("Display Artwork")

    val currentPage: MutableLiveData<Int> = mutableLiveDataOf()

    fun onPickButtonClick() {
        pickButtonText.value = "Pick a photo"
        optionsButtonText.value = "Options"
        currentPage.value = 0
    }

    fun onOptionsButtonClick() {
        currentPage.value = 1
        pickButtonText.value = "Pick"
        optionsButtonText.value = "Art Options"
    }

    fun onCreateButtonClick() {

    }

    fun onDisplayButtonClick() {

    }

    fun photoPickedByChild() {
        optionsButtonVisible.value = true

        currentPage.value = 1
        pickButtonText.value = "Pick"
        optionsButtonText.value = "Art Options"
    }

}