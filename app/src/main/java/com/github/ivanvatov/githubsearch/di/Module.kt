package com.github.ivanvatov.githubsearch.di

import com.github.ivanvatov.githubsearch.api.BackendApi
import com.github.ivanvatov.githubsearch.jsonInstance
import com.github.ivanvatov.githubsearch.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideRepository() = Repository(BackendApi(HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(jsonInstance)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
        }
    }))
}