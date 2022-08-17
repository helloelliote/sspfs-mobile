package kr.djgis.sspfs.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    private val _throwable = MutableLiveData<Throwable>()
    val throwable get() = _throwable

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        _throwable.postValue(t)
    }

    private val ioDispatchers = Dispatchers.IO + coroutineExceptionHandler

    fun CoroutineScope.safeLaunch(
        coroutineContext: CoroutineContext = ioDispatchers,
        launchBody: suspend () -> Unit,
    ): Job {
        return this.launch(coroutineContext) {
            launchBody.invoke()
        }
    }
}
