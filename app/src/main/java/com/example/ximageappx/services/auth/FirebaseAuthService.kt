package com.example.ximageappx.services.auth

import com.example.ximageappx.services.firebase.FirebaseDatabaseService
import com.example.ximageappx.services.firebase.IDatabaseService
import com.google.android.gms.tasks.Task
import com.google.common.primitives.ImmutableDoubleArray
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ServiceScoped
import java.lang.Exception
import javax.inject.Inject

@ServiceScoped
class FirebaseAuthService @Inject constructor(
    private val databaseService: IDatabaseService
): IAuthService {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

//    @Inject
//    lateinit var databaseService: IDatabaseService

    override fun authenticate(
        email: String,
        password: String,
        callback: (Task<AuthResult>) -> Unit
    ) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(callback)
    }

    override fun register(
        email: String,
        password: String,
        login: String,
        callback: () -> Unit
    ) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                databaseService.createUserWithEmailAndLogin(email, login)
                callback()
            } else {
                throw RegisterFailedException("Register failed!")
            }
        }

    }

    override fun getCurrentUser(): FirebaseUser? {
        return mAuth.currentUser
    }

    override fun signOut() {
        mAuth.signOut()
    }

    override fun resetPass() {
        mAuth.sendPasswordResetEmail(getCurrentUser()!!.email!!)
    }
}