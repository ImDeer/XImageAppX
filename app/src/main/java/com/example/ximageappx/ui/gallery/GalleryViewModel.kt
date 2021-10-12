package com.example.ximageappx.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.ximageappx.data.FirebasePagingSource
import com.google.firebase.firestore.FirebaseFirestore

class GalleryViewModel : ViewModel() {

    val photos = Pager(PagingConfig(pageSize = 10, maxSize = 100, enablePlaceholders = false)) {
        FirebasePagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)
}