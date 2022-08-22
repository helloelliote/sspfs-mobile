package kr.djgis.sspfs.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel() {

    private val _throwable = MutableLiveData<Throwable>()
    val throwable get() = _throwable

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        t.printStackTrace()
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
