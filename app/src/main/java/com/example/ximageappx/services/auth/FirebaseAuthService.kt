package com.example.ximageappx.services.auth

import com.example.ximageappx.services.IFirebaseService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Inject

@ServiceScoped
class FirebaseAuthService //@Inject constructor(
//    private val firebaseService: IFirebaseService
//)
: IAuthService {
//    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

//    @Inject
//    lateinit var databaseService: IDatabaseService

//    override fun authenticate(
//        email: String,
//        password: String,
//        callback: (Task<AuthResult>) -> Unit
//    ) {
//        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback)
//    }

//    override fun register(
//        email: String,
//        password: String,
//        login: String,
//        callback: () -> Unit
//    ) {
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
//            if (it.isSuccessful) {
//                firebaseService.createUserWithEmailAndLogin(email, login)
//                callback()
//            } else {
//                throw RegisterFailedException("Register failed!")
//            }
//        }
//
//    }

//    override fun getCurrentUser(): FirebaseUser? {
//        return mAuth.currentUser
//    }

//    override fun signOut() {
//        mAuth.signOut()
//    }
//
//    override fun resetPass() {
//        mAuth.sendPasswordResetEmail(getCurrentUser()!!.email!!)
//    }
}