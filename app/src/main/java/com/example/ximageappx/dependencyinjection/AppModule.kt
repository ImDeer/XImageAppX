package com.example.ximageappx.dependencyinjection

import com.example.ximageappx.services.auth.FirebaseAuthService
import com.example.ximageappx.services.auth.IAuthService
import com.example.ximageappx.services.firebase.FirebaseDatabaseService
import com.example.ximageappx.services.firebase.IDatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

//    @Binds
//    abstract fun bindIAuthService(firebaseAuthService: FirebaseAuthService): IAuthService
//
//    @Binds
//    abstract fun bindIDatabaseService(firebaseDatabaseService: FirebaseDatabaseService): IDatabaseService

    @Singleton
    @Provides
    fun provideIDatabaseService(authService: FirebaseAuthService): IDatabaseService = FirebaseDatabaseService(authService)

    @Singleton
    @Provides
    fun provideIAuthService(databaseService: FirebaseDatabaseService): IAuthService = FirebaseAuthService(databaseService)


}
//    // returns a Retrofit instance
//    @Provides
//    @Singleton
//    fun provideRetrofit(): Retrofit =
//        Retrofit.Builder()
//            .baseUrl(UnsplashApi.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
////    @Provides
////    @Singleton
////    fun provideUnsplashApi(retrofit: Retrofit):UnsplashApi=
////        retrofit.create(UnsplashApi::class.java)
//}