package com.example.ximageappx.ui

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentRegisterBinding
import com.example.ximageappx.services.auth.FirebaseAuthService
import com.example.ximageappx.services.auth.IAuthService
import com.example.ximageappx.services.auth.RegisterFailedException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class RegisterFragment @Inject constructor(
    private val authService: IAuthService
): Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val authService: IAuthService = FirebaseAuthService()
//        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
//        val mUser = Firebase.database.getReference("users")

        val binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            setHasOptionsMenu(false)

            tvSignIn.setOnClickListener {
                val action = RegisterFragmentDirections.actionRegisterFragmentToLogInFragment()
                findNavController().navigate(action)
            }

            btRegister.setOnClickListener {
                val email = inputRegEmail.text.toString().trim { it <= ' ' }
                val login = inputRegLogin.text.toString().trim { it <= ' ' }
                val pass = inputRegPass.text.toString().trim { it <= ' ' }

                if (email.isNotEmpty() && login.isNotEmpty() && pass.isNotEmpty())
//                    mAuth.createUserWithEmailAndPassword(email, pass)
//                        .addOnCompleteListener {
                    authService.register(email, pass, login) {
                        try {
//                            if (it.isSuccessful) {
                            Toast.makeText(context, "SignUp successful", Toast.LENGTH_SHORT)
                                .show()
//                                mUser.child(mAuth.currentUser!!.uid).child("login").setValue(login)
//                                mUser.child(mAuth.currentUser!!.uid).child("email").setValue(email)
                            val action =
                                RegisterFragmentDirections.actionRegisterFragmentToGalleryFragment()
                            findNavController().navigate(action)
                        } catch (e: RegisterFailedException) {
                            Toast.makeText(
                                context,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
//                                Log.e(ContentValues.TAG, "SignUp failed", it.exception)
                        }
                    }
            }


        }
    }

}