package com.example.ximageappx.data

import android.util.Log
import androidx.paging.PagingSource
//import com.example.ximageappx.api.UnsplashApi
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException

private const val UNSPLASH_STARTING_PAGE_INDEX = 1

class FirebasePagingSource(
    private val db: FirebaseFirestore
) : PagingSource<QuerySnapshot, PhotoPost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PhotoPost> {
        return try {
            // Step 1
//            Log.d("firebasePagingSource", "load")

            val currentPage = params.key ?: db.collection("posts").orderBy("created", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
//            Log.d("firebasePagingSource", "currentPage")

            // Step 2
            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
//            Log.d("firebasePagingSource", "currentPage.toObjs")
//
//            var tmp = currentPage.toObjects(PhotoPost::class.java)
//            Log.d("firebasePagingSource", "toObjs")
//
//             Step 3
            val nextPage = db.collection("posts").limit(10).startAfter(lastDocumentSnapshot)
                .get()
                .await()

//            Log.d("pagingSource", "${currentPage.toObjects(PhotoPost::class.java)}")

            // Step 4
            LoadResult.Page(
                data = currentPage.toObjects(PhotoPost::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
//            Log.e("firebasePagingSource", "error: $e")

            LoadResult.Error(e)
        }
    }
}
//    private val db: FirebaseDatabase// = FirebaseDatabase.getInstance()
//) : PagingSource<Int, PhotoPost>() {
//
//    //Paging 3 takes care of calling this method
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoPost> {
//
//        return try {
//            val position = params.key ?: db.reference.child("posts").limit(10)
//
//            LoadResult.Page(
//                data = position,
//                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
//                nextKey = if (photos.isEmpty()) null else position + 1
//            )
//        } catch (exception: IOException) {
//            LoadResult.Error(exception)
//        } catch (exception: HttpException) {
//            LoadResult.Error(exception)
//        }
//    }

//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoPost> {
//
//        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX
//
//        return try {
//            val photos = if (query!="") {
//                val response = unsplashApi.searchPhotos(query, position, 5)//params.loadSize)
//                response.results
//            } else {
//                val response = unsplashApi.getPhotos(position, params.loadSize)
//                response
//            }
//            LoadResult.Page(
//                data = photos,
//                prevKey = if (position == UNSPLASH_STARTING_PAGE_INDEX) null else position - 1,
//                nextKey = if (photos.isEmpty()) null else position + 1
//            )
//        } catch (exception: IOException) {
//            LoadResult.Error(exception)
//        } catch (exception: HttpException) {
//            LoadResult.Error(exception)
//        }
//    }


//}
