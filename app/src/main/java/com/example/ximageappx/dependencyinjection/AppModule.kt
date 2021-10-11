package com.example.ximageappx.dependencyinjection

import com.example.ximageappx.services.auth.FirebaseAuthService
import com.example.ximageappx.services.auth.IAuthService
import com.example.ximageappx.services.FirebaseService
import com.example.ximageappx.services.IFirebaseService
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
    fun provideIDatabaseService(): IFirebaseService = FirebaseService()//authService)
//    fun provideIDatabaseService(authService: FirebaseAuthService): IFirebaseService = FirebaseService()//authService)

//    @Singleton
//    @Provides
//    fun provideIAuthService(databaseService: FirebaseService): IAuthService = FirebaseAuthService(databaseService)


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