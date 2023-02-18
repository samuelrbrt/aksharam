package `in`.digistorm.aksharam.config.state

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import `in`.digistorm.aksharam.config.state.State.Cached
import `in`.digistorm.aksharam.config.state.State.Error
import `in`.digistorm.aksharam.config.state.State.Init
import `in`.digistorm.aksharam.config.state.State.Loading
import `in`.digistorm.aksharam.config.state.State.Success

open class LiveState<T> : LiveData<State<T>>()

class StateCallbackBuilder<T> {
    var onSuccess: (T?) -> Unit = {}
    var onLoading: (Boolean, Int) -> Unit = { _, _ -> }
    var onError: (Throwable) -> Unit = {}
    var onCached: (T?) -> Unit = {}

    fun onSuccess(success: (T?) -> Unit): StateCallbackBuilder<T> {
        onSuccess = success
        return this
    }

    fun onLoading(loading: (Boolean, Int) -> Unit): StateCallbackBuilder<T> {
        onLoading = loading
        return this
    }

    fun onError(error: (Throwable) -> Unit): StateCallbackBuilder<T> {
        onError = error
        return this
    }

    fun onCached(cached: (T?) -> Unit): StateCallbackBuilder<T> {
        onCached = cached
        return this
    }
}

fun <T> LiveData<State<T>>.observe(lifecycleOwner: LifecycleOwner): StateCallbackBuilder<T> {
    val builder = StateCallbackBuilder<T>()

    observe(lifecycleOwner) {
        when (it) {
            is Cached -> builder.onCached(it.data)
            is Error -> builder.onError(it.exception)
            is Init -> {}
            is Loading -> builder.onLoading(true, it.progress)
            is Success -> builder.onSuccess(it.data)
        }

        if (it !is Loading) builder.onLoading(false, 100)
    }

    return builder
}