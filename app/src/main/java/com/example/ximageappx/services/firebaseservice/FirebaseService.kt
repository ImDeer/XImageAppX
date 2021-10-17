package com.example.ximageappx.services.firebaseservice

import android.net.Uri
import com.example.ximageappx.data.PhotoPost
import com.example.ximageappx.data.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.scopes.ServiceScoped
import java.sql.Timestamp
import java.util.*


@ServiceScoped
class FirebaseService : IFirebaseService {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val mDB = Firebase.database.getReference("/")

    private val mFirestore = FirebaseFirestore.getInstance().collection("posts")

    // region firebaseDB
    override fun setUserLogin(login: String) {
        mDB.child("users").child(getCurrentUser()!!.uid).child("login").setValue(login)
    }

    override fun updateUser(callback: (user: User) -> Unit) {
        mDB.child("users").child(getCurrentUser()!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)!!
                    callback(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun setProfilePhoto(uri: Uri?) {
        uploadImageToFirebaseStorage(uri) {
            mDB.child("users").child(getCurrentUser()!!.uid).child("profilePhotoUrl")
                .setValue(it.toString())
        }
    }

    override fun uploadImageToFirebaseStorage(uri: Uri?, callback: (uri: Uri) -> Unit) {
        if (uri == null) return
        val filename = UUID.randomUUID().toString()
        val mStorage = FirebaseStorage.getInstance().getReference("/images/$filename")
        mStorage.putFile(uri).addOnSuccessListener {
            mStorage.downloadUrl.addOnSuccessListener {
                callback(it)
            }
        }
    }

    override fun createUserWithEmailAndLogin(email: String, login: String) {
        mDB.child("users").child(getCurrentUser()!!.uid).child("login").setValue(login)
        mDB.child("users").child(getCurrentUser()!!.uid).child("email").setValue(email)

    }

    override fun listenToPhotoCreator(uid: String, callback: (user: User) -> Unit) {
        mDB.child("users").child(uid)
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
            mDB.child("likes").child(getCurrentUser()!!.uid).child(photoId).setValue(true)
        else
            mDB.child("likes").child(getCurrentUser()!!.uid).child(photoId).removeValue()

    }

    override fun getLikedState(
        photoId: String,
        callback: (liked: Boolean) -> Unit,
        callback2: () -> Unit
    ) {
        mDB.child("likes").child(getCurrentUser()!!.uid).child(photoId)
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

    // endregion

    override fun createPost(uri: Uri, description: String, callback: () -> Unit) {
        uploadImageToFirebaseStorage(uri) {
            val newPostRef = mFirestore.document()
            val newPost = PhotoPost(
                newPostRef.id,
                description,
                Timestamp(System.currentTimeMillis()).toString(),
                getCurrentUser()!!.uid,
                it.toString()
            )
            newPostRef.set(newPost)
            callback()
        }
    }

    // region authServicePart
    override fun getCurrentUser(): FirebaseUser? = mAuth.currentUser

    override fun isAuthenticated(): Boolean = getCurrentUser() != null

    override fun signOut() = mAuth.signOut()

    override fun resetPass() {
        mAuth.sendPasswordResetEmail(getCurrentUser()!!.email!!)
    }

    override fun register(
        email: String,
        password: String,
        login: String,
        callback: () -> Unit,
        callback2: (errorMessage: String) -> Unit
    ) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                createUserWithEmailAndLogin(email, login)
                callback()
            } else {
                callback2(it.exception?.message ?: "")
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
