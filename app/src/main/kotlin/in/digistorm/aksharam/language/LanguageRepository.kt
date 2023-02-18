package `in`.digistorm.aksharam.language

interface LanguageRepository {

    suspend fun getSupportLanguages(): List<SupportedLanguage>
}