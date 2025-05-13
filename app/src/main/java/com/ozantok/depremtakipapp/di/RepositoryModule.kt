package com.ozantok.depremtakipapp.di


import com.ozantok.depremtakipapp.data.remote.EarthquakeApiService
import com.ozantok.depremtakipapp.data.repository.EarthquakeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideEarthquakeRepository(
        earthquakeApiService: EarthquakeApiService
    ): EarthquakeRepository {
        return EarthquakeRepository(earthquakeApiService)
    }
}