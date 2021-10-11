package com.example.ximageappx.services

import android.net.Uri
import android.util.Log
import com.example.ximageappx.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ServiceScoped
import java.util.*

@ServiceScoped
class FirebaseService : IFirebaseService {
//@Inject constructor(
//    private val authService: IAuthService
//) : IFirebaseService {

//    @Inject
//    lateinit var authService: IAuthService

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val dbRef =
        Firebase.database.getReference("/")//users")// + getCurrentUser()!!.uid)

//    private val likesRef = Firebase.database.getReference("likes")

    //region dbServicePart
    override fun setUserLogin(login: String) {
        dbRef.child("users").child(getCurrentUser()!!.uid).child("login").setValue(login)
    }


    override fun updateUser(callback: (user: User) -> Unit) {
        dbRef.child("users").child(getCurrentUser()!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)!!
                    callback(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(
                        "FirebaseService",
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
                dbRef.child("users").child(getCurrentUser()!!.uid).child("profilePhotoUrl")
                    .setValue(it.toString())
            }
        }
    }

    override fun createUserWithEmailAndLogin(email: String, login: String) {
        dbRef.child("users").child(getCurrentUser()!!.uid).child("login").setValue(login)
        dbRef.child("users").child(getCurrentUser()!!.uid).child("email").setValue(email)

    }

    override fun listenToPhotoCreator(uid: String, callback: (user: User) -> Unit) {
        dbRef.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if (dataSnapshot.exists())// && dataSnapshot.value == true
                    {
                        val user = User(
                            login = dataSnapshot.child("login").value.toString(),
                            profilePhotoUrl = dataSnapshot.child("profilePhotoUrl").value.toString()
                        )
                        callback(user)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun setLikedValue(photoId: String, liked: Boolean) {
        if (!liked)
            dbRef.child("likes").child(getCurrentUser()!!.uid).child(photoId).setValue(true)
        else
            dbRef.child("likes").child(getCurrentUser()!!.uid).child(photoId).removeValue()
    }

    override fun getLikedState(
        photoId: String,
        callback: (liked: Boolean) -> Unit,
        callback2: () -> Unit
    ) {
        dbRef.child("likes").child(getCurrentUser()!!.uid).child(photoId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    callback(dataSnapshot.exists() && dataSnapshot.value == true)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback2()
                }

            })

    }

    //endregion

    //region authServicePart
    override fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun resetPass() {
        mAuth.sendPasswordResetEmail(getCurrentUser()!!.email!!)
    }

    override fun register(
        email: String,
        password: String,
        login: String,
        callback: () -> Unit
    ) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                createUserWithEmailAndLogin(email, login)
                callback()
            } else {
                throw RegisterFailedException("Register failed!")
            }
        }

    }

    override fun authenticate(
        email: String,
        password: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback)
    }
    //endregion
}