package `in`.digistorm.aksharam.language

import `in`.digistorm.aksharam.config.coroutine.Dispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LanguageModule {

    @Provides
    @Singleton
    fun languageApiProvider(retrofit: Retrofit): LanguageApi {
        return retrofit.create(LanguageApi::class.java)
    }

    @Provides
    @Singleton
    fun languageRepoProvider(api: LanguageApi, dispatchers: Dispatchers): LanguageRepository {
        return LanguageRepoImpl(api, dispatchers)
    }
}