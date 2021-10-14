package com.example.ximageappx.dependencyinjection

import com.example.ximageappx.services.firebaseservice.FirebaseService
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideIDatabaseService(): IFirebaseService = FirebaseService()
}
