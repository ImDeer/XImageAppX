package com.example.ximageappx.ui

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LogInFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        val binding = FragmentLoginBinding.bind(view)

        binding.apply {
            bt_login.setOnClickListener {
                val email = input_email.text.toString().trim { it <= ' ' }
                val pass = input_pass.text.toString().trim { it <= ' ' }

//                when {
//                    TextUtils.isEmpty(email) -> {
//                        Toast.makeText(context, "Please enter Email", Toast.LENGTH_SHORT).show()
//                    }
//                    TextUtils.isEmpty(pass) -> {
//                        Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
//                    }
//                    else -> {
                if (email.isNotEmpty() && pass.isNotEmpty())
                    mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(context, "LogIn successful", Toast.LENGTH_SHORT)
                                    .show()
                                val action =
                                    LogInFragmentDirections.actionLogInFragmentToGalleryFragment()
                                findNavController().navigate(action)
                            } else {
                                Toast.makeText(context, "LogIn failed", Toast.LENGTH_SHORT)
                                    .show()
                                Log.e(TAG, "LogIn failed", it.exception)
                            }
                        }
            }


        }
    }

}