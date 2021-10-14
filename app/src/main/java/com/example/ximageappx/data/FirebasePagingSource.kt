package com.example.ximageappx.data

import androidx.paging.PagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class FirebasePagingSource(
    private val db: FirebaseFirestore
) : PagingSource<QuerySnapshot, PhotoPost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PhotoPost> {
        return try {
            val currentPage =
                params.key ?: db.collection("posts")
                    .orderBy("created", Query.Direction.DESCENDING)
                    .get()
                    .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = db.collection("posts")
                .orderBy("created", Query.Direction.DESCENDING)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(PhotoPost::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}