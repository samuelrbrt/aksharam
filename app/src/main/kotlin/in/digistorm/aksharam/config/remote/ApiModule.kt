package `in`.digistorm.aksharam.config.remote

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun okHttpCacheProvider(@ApplicationContext context: Context): Cache {
        val cacheSize = 50 * 1024 * 1024L // 50 MiB
        return Cache(context.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun serializerProvider(): Gson = GsonBuilder()
        .enableComplexMapKeySerialization()
        .setLenient()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .setPrettyPrinting()
        .setVersion(1.0)
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        .create()

    @Provides
    @Singleton
    fun converterFactoryProvider(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    fun httpClientProvider(cache: Cache, serializer: Gson): OkHttpClient {
        val logger = HttpLoggingInterceptor(PrettyJSONLogger(serializer))
            .apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(logger)
            .connectTimeout(10, SECONDS)
            .readTimeout(30, SECONDS)
            .writeTimeout(30, SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun retrofitProvider(factory: GsonConverterFactory, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(RemoteConfig.BASE_URL)
            .addConverterFactory(factory)
            .client(client)
            .build()
    }
}