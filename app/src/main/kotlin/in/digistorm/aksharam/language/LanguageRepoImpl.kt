package `in`.digistorm.aksharam.language

import `in`.digistorm.aksharam.config.coroutine.Dispatchers
import kotlinx.coroutines.withContext

class LanguageRepoImpl(
    private val api: LanguageApi,
    private val dispatchers: Dispatchers
) : LanguageRepository {

    override suspend fun getSupportLanguages(): List<SupportedLanguage> {
        return withContext(dispatchers.IO) {
            return@withContext api.getSupportLanguage()
        }
    }
}