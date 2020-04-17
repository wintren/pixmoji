package rocks.wintren.pixmoji

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

val mainScheduler: Scheduler get() = AndroidSchedulers.mainThread()
val ioScheduler: Scheduler get() = Schedulers.io()
val computationScheduler: Scheduler get() = Schedulers.computation()