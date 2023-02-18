package `in`.digistorm.aksharam.language

import retrofit2.http.GET

interface LanguageApi {

    @GET("contents?ref=1.0")
    suspend fun getSupportLanguage(): List<SupportedLanguage>
}