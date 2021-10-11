package com.example.ximageappx.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class FirebasePagingSource(
    private val db: FirebaseFirestore
) : PagingSource<QuerySnapshot, PhotoPost>() {

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, PhotoPost> {
        return try {
            // Step 1
//            Log.d("firebasePagingSource", "load")

            val currentPage =
                params.key ?: db.collection("posts")
                    .orderBy("created", Query.Direction.DESCENDING)
//                    .limit(5)
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
            val nextPage = db.collection("posts")
                .orderBy("created", Query.Direction.DESCENDING)
//                .limit(5)
                .startAfter(lastDocumentSnapshot)
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

//) : PagingSource<Int, PhotoPost>() {
//
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoPost> {
////        val pageIndex = params.key ?: STARTING_PAGE_INDEX
//
//        return try {
//            val response = params.key ?: db.collection("posts").orderBy("created", Query.Direction.DESCENDING)
//                .limit(10)
//                .get()
////                .await()
//            val photos = response.toObjects(PhotoPost::class.java)
//            val nextKey =
//                if (photos.isEmpty()) {
//                    null
//                } else {
//                    // By default, initial load size = 3 * NETWORK PAGE SIZE
//                    // ensure we're not requesting duplicating items at the 2nd request
//                    pageIndex + (params.loadSize / 5)
//                }
//            LoadResult.Page(
//                data = photos,
//                prevKey = if (pageIndex == STARTING_PAGE_INDEX) null else pageIndex - 1,
//                nextKey = if(photos.isEmpty()) null else pageIndex + 1
//            )
//        } catch (exception: IOException) {
//            return LoadResult.Error(exception)
//        }
//    }
//
//    /**
//     * The refresh key is used for subsequent calls to PagingSource.Load after the initial load.
//     */
//    @ExperimentalPagingApi
//    override fun getRefreshKey(state: PagingState<Int, PhotoPost>): Int? {
//        // We need to get the previous key (or next key if previous is null) of the page
//        // that was closest to the most recently accessed index.
//        // Anchor position is the most recently accessed index.
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
//}


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
