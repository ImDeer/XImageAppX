package com.example.ximageappx.services.firebase

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.ximageappx.data.User
import com.example.ximageappx.services.auth.FirebaseAuthService
import com.example.ximageappx.services.auth.IAuthService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ServiceScoped
import java.util.*
import javax.inject.Inject

@ServiceScoped
class FirebaseDatabaseService @Inject constructor(
    private val authService: IAuthService
)
    : IDatabaseService {
//    @Inject
//    lateinit var authService: IAuthService

    private val usersRef =
        Firebase.database.getReference("users/" + authService.getCurrentUser()!!.uid)

    override fun setUserLogin(login: String) {
        usersRef.child("login").setValue(login)
    }

    override fun updateUser(callback: (user: User) -> Unit) {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)!!
                callback(user)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(
                    ContentValues.TAG,
                    "onCancelled: Something went wrong! Error:" + databaseError.message
                )
            }
        })
    }

    override fun uploadImageToFirebaseStorage(uri: Uri?, callback: () -> Unit) {
        if (uri == null) return
        val filename = UUID.randomUUID().toString()
        val mStorage = FirebaseStorage.getInstance().getReference("/images/$filename")
        mStorage.putFile(uri).addOnSuccessListener {
            callback()
            mStorage.downloadUrl.addOnSuccessListener {
                usersRef.child("profilePhotoUrl").setValue(it.toString())
            }
        }
    }

    override fun createUserWithEmailAndLogin(email: String, login: String) {
        usersRef.child(authService.getCurrentUser()!!.uid).child("login").setValue(login)
        usersRef.child(authService.getCurrentUser()!!.uid).child("email").setValue(email)

    }
}