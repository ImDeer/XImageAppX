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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val mUser = Firebase.database.getReference("users")

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
                    mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "SignUp successful", Toast.LENGTH_SHORT)
                                    .show()
                                mUser.child(mAuth.currentUser!!.uid).child("login").setValue(login)
                                val action =
                                    RegisterFragmentDirections.actionRegisterFragmentToGalleryFragment()
                                findNavController().navigate(action)
                            } else {
                                Toast.makeText(
                                    context,
                                    "SignUp failed" + it.exception,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                Log.e(ContentValues.TAG, "SignUp failed", it.exception)
                            }
                        }
            }


        }
    }

}