package rocks.wintren.pixmoji

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseViewModel: ViewModel() {

    private val disposables = CompositeDisposable()

    fun Disposable.clearWithViewModel() {
        disposables.add(this)
    }

    override fun onCleared() {
        disposables.clear()
    }

}