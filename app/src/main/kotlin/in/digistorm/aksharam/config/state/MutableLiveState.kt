package `in`.digistorm.aksharam.config.state

import `in`.digistorm.aksharam.config.state.State.Cached
import `in`.digistorm.aksharam.config.state.State.Error
import `in`.digistorm.aksharam.config.state.State.Loading
import `in`.digistorm.aksharam.config.state.State.Success

class MutableLiveState<T> : LiveState<T>() {
    fun postCached(data: T) {
        postValue(Cached(data))
    }

    fun postSuccess(data: T?) {
        postValue(Success(data))
    }

    fun postError(error: Exception) {
        postValue(Error(error))
    }

    fun postLoading(progress: Int = 0) {
        postValue(Loading(progress))
    }
}