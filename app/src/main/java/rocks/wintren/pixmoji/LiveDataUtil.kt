package rocks.wintren.pixmoji

import androidx.lifecycle.MutableLiveData


fun <T> mutableLiveDataOf(value: T? = null): MutableLiveData<T> {
    return MutableLiveData<T>().apply { this.value = value }
}