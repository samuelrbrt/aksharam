package `in`.digistorm.aksharam.config.base

import `in`.digistorm.aksharam.config.state.State
import `in`.digistorm.aksharam.config.state.State.Loading
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

fun <T> Flow<T>.asLiveState(vmScope: CoroutineScope, initState: State<T>): LiveData<State<T>> {
    return map { State.Success(it) as State<T> }
        .catch { error -> emit(State.Error(error)) }
        .stateIn(vmScope, SharingStarted.Lazily, initState)
        .asLiveData()
}

open class BaseViewModel : ViewModel() {
    fun <T> Flow<T>.asScopedLiveState(
        scope: CoroutineScope = viewModelScope,
        initState: State<T> = Loading()
    ): LiveData<State<T>> {
        return asLiveState(scope, initState)
    }
}