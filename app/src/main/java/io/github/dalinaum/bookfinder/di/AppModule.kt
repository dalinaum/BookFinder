package io.github.dalinaum.bookfinder.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.dalinaum.bookfinder.api.GoogleBooksService
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    @Named("API_URI")
    fun provideGoogleBooksAPI() = "https://www.googleapis.com/books/v1/"

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideConverterFactory(
        gson: Gson
    ): Converter.Factory = GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideRetrofit(
        @Named("API_URI") apiUrl: String,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder().baseUrl(apiUrl).addConverterFactory(converterFactory).build()

    @Singleton
    @Provides
    fun provideGithubService(
        retrofit: Retrofit
    ): GoogleBooksService = retrofit.create(GoogleBooksService::class.java)
}