package com.example.ximageappx.ui.gallery

//import androidx.hilt.Assisted
//import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.ximageappx.data.FirebasePagingSource
//import com.example.ximageappx.data.UnsplashRepository
import com.google.firebase.firestore.FirebaseFirestore

class GalleryViewModel : ViewModel(){ //@ViewModelInject constructor(
//    private val repository: UnsplashRepository,

//    @Assisted state: SavedStateHandle
//) : ViewModel() {

    val photos = Pager(PagingConfig(pageSize = 10, maxSize = 100, enablePlaceholders = false)) {
        FirebasePagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)


//    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_QUERY)

//    val photos =

//    val photos = currentQuery.switchMap { queryString ->
//        repository.getSearchResults(queryString).cachedIn(viewModelScope)
//    }

//    fun searchPhotos(query:String){
//        currentQuery.value = query
//    }
//
//    companion object{
//        private const val CURRENT_QUERY = "current_query"
//        private const val DEFAULT_QUERY = ""
//    }
}