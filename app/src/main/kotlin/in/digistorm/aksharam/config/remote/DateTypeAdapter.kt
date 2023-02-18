package `in`.digistorm.aksharam.config.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateTypeAdapter : JsonDeserializer<Date> {
    private val dateFormats = arrayOf("MM/dd/yyyy", "yyyy-MM-dd HH:mm:ss")
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date? {
        for (format in dateFormats) {
            try {
                return SimpleDateFormat(format).parse(json.asString)
            } catch (_: ParseException) {
                // APIs return multiple date formats
            }
        }

        return null
    }
}