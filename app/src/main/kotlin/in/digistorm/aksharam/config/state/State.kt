package `in`.digistorm.aksharam.config.state

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class State<out T> {

    class Init<T> : State<T>()

    data class Loading(val progress: Int = 0) : State<Nothing>()

    data class Success<out T>(val data: T?) : State<T>()

    data class Error(val exception: Throwable) : State<Nothing>()

    data class Cached<out T>(val data: T) : State<T>()

    fun isLoading() = this is Loading

    fun isError() = this is Error

    fun isEmptySuccess() = this is Success && ((data is List<*> && data.isEmpty()) || data == null)

    fun getError() = if (this is Error) this.exception.message else null

    fun isSuccess() = this is Success

    fun isCached() = this is Cached

    fun asSuccess() = this as Success

    override fun toString(): String {
        return when (this) {
            is Init -> "Init[]"
            is Cached<*> -> "Cached[data=$data]"
            is Loading -> "Loading[]"
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}