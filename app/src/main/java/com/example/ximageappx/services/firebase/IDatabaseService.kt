package com.example.ximageappx.services.firebase

import android.net.Uri
import com.example.ximageappx.data.User
import dagger.hilt.android.AndroidEntryPoint

interface IDatabaseService {
    fun setUserLogin(login: String)

    fun updateUser(callback: (user: User) -> Unit)

    fun uploadImageToFirebaseStorage(uri: Uri?, callback: () -> Unit)

    fun createUserWithEmailAndLogin(email:String, login:String)
}