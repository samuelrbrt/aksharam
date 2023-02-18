package `in`.digistorm.aksharam.config.coroutine

import kotlinx.coroutines.CoroutineDispatcher

@Suppress("PropertyName")
interface Dispatchers {
    val Main: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Default: CoroutineDispatcher
    val Unconfined: CoroutineDispatcher
}
