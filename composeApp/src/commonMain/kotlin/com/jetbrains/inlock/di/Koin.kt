package com.jetbrains.inlock.di

import com.jetbrains.inlock.data.FirebaseService
import com.jetbrains.inlock.data.FirebaseServiceImpl
import com.jetbrains.inlock.data.ProductRepository
import com.jetbrains.inlock.data.ProductRepositoryImpl
import com.jetbrains.inlock.screens.assets.AssetsViewModel
import com.jetbrains.inlock.screens.auth.LoginViewModel
import com.jetbrains.inlock.screens.detail.AssetDetailViewModel
import com.jetbrains.inlock.screens.profile.ProfileViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                json(json, contentType = ContentType.Any)
            }
        }
    }

    singleOf(::FirebaseServiceImpl) bind FirebaseService::class
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
}

val viewModelModule = module {
    factoryOf(::LoginViewModel)
    factoryOf(::AssetsViewModel)
    factoryOf(::AssetDetailViewModel)
    factoryOf(::ProfileViewModel)
}

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
