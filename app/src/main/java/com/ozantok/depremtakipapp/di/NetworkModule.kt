package com.ozantok.depremtakipapp.di
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ozantok.depremtakipapp.data.model.EarthquakeResponse
import com.ozantok.depremtakipapp.data.remote.EarthquakeAdapter
import com.ozantok.depremtakipapp.data.remote.EarthquakeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(List::class.java, EarthquakeAdapter())
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        // Türkiye deprem verisi API'leri
        return Retrofit.Builder()
            // Alternatif deprem API'si - GitHub üzerinde açık kaynaklı bir proje
            .baseUrl("https://api.orhanaydogdu.com.tr/deprem/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideEarthquakeApiService(retrofit: Retrofit): EarthquakeApiService {
        return retrofit.create(EarthquakeApiService::class.java)
    }
}