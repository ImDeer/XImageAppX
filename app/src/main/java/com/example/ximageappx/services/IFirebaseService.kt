package com.example.ximageappx.services

import android.net.Uri
import com.example.ximageappx.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

interface IFirebaseService {
    fun setUserLogin(login: String)

    fun updateUser(callback: (user: User) -> Unit)

    fun uploadImageToFirebaseStorage(uri: Uri?, callback: () -> Unit)

    fun createUserWithEmailAndLogin(email: String, login: String)

    fun authenticate(email: String, password: String, callback: (Task<AuthResult>) -> Unit)
    fun register(
        email: String,
        password: String,
        login: String,
        callback: () -> Unit
    )

    fun getLikedState(
        photoId: String,
        callback: (liked: Boolean) -> Unit,
        callback2: () -> Unit
    )

    fun setLikedValue(photoId: String, liked: Boolean)


    fun getCurrentUser(): FirebaseUser?
    fun signOut()
    fun resetPass()
    fun listenToPhotoCreator(uid: String, callback: (user: User) -> Unit)
}