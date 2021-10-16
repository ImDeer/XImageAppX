package com.example.ximageappx.services.firebaseservice

import android.net.Uri
import com.example.ximageappx.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface IFirebaseService {
    // region firebaseDB
    fun setUserLogin(login: String)
    fun updateUser(callback: (user: User) -> Unit)
    fun setProfilePhoto(uri: Uri?)
    fun uploadImageToFirebaseStorage(uri: Uri?, callback: (uri: Uri) -> Unit)
    fun createUserWithEmailAndLogin(email: String, login: String)
    fun listenToPhotoCreator(uid: String, callback: (user: User) -> Unit)
    fun setLikedValue(photoId: String, liked: Boolean)
    fun getLikedState(
        photoId: String,
        callback: (liked: Boolean) -> Unit = {},
        callback2: () -> Unit = {}
    )
    // endregion

    // region fi
    fun createPost(uri: Uri, description: String)

    // region firebaseAuth
    fun getCurrentUser(): FirebaseUser?
    fun isAuthenticated(): Boolean
    fun signOut()
    fun resetPass()
    fun register(
        email: String,
        password: String,
        login: String,
        callback: () -> Unit,
        callback2: (errorMessage: String) -> Unit
    )

    fun authenticate(email: String, password: String, callback: (Task<AuthResult>) -> Unit)
    // endregion
}