package com.example.ximageappx.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
//import com.example.ximageappx.api.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
//class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {
//
//    fun getSearchResults(query: String) =
//        Pager(
//            config = PagingConfig(
//                pageSize = 20,
//                maxSize = 100,
//                enablePlaceholders = false
//            ),
//            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, if(query != "") query else "latest" )}
//        ).liveData
//}