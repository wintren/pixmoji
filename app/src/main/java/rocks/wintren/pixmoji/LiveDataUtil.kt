package rocks.wintren.pixmoji

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


fun <T> mutableLiveDataOf(value: T? = null): MutableLiveData<T> {
    return MutableLiveData<T>().apply { this.value = value }
}

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) {
    this.observe(owner, Observer(observer))
}
