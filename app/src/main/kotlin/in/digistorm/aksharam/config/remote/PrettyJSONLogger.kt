package `in`.digistorm.aksharam.config.remote

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class PrettyJSONLogger(private val serializer: Gson) : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val prettyPrintJson = serializer.toJson(JsonParser.parseString(message))
                Timber.tag(TAG).d(prettyPrintJson)
            } catch (m: JsonSyntaxException) {
                Timber.tag(TAG).d(message)
            }
        } else {
            Timber.tag(TAG).d(message)
        }
    }

    companion object {
        private const val TAG = "OkHttp"
    }
}