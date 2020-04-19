package rocks.wintren.pixmoji

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

val mainThread: Scheduler get() = AndroidSchedulers.mainThread()
val newThread: Scheduler get() = Schedulers.newThread()
val ioThread: Scheduler get() = Schedulers.io()
val computationThread: Scheduler get() = Schedulers.computation()