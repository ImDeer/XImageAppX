package com.example.ximageappx.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentLoginBinding
import com.example.ximageappx.isEmailValid
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import com.example.ximageappx.showToast
import javax.inject.Inject

class LogInFragment @Inject constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLoginBinding.bind(view)

        binding.apply {
            setHasOptionsMenu(false)

            tvSignUp.setOnClickListener {
                val action = LogInFragmentDirections.actionLogInFragmentToRegisterFragment()
                findNavController().navigate(action)
            }

            btLogin.setOnClickListener {
                val email = inputEmail.text.toString().trim { it <= ' ' }
                val pass = inputPass.text.toString().trim { it <= ' ' }

                if (email.isEmailValid() && pass.isNotEmpty()) {
                    progressBar.isVisible = true
                    btLogin.isClickable = false
                    tvSignUp.isClickable = false
                    inputEmail.isFocusable = false
                    inputPass.isFocusable = false
                    firebaseService.authenticate(email, pass) {
                        if (it.isSuccessful) {
                            context?.showToast("LogIn successful")
                            val action =
                                LogInFragmentDirections.actionLogInFragmentToGalleryFragment()
                            findNavController().navigate(action)
                        } else {
                            context?.showToast("LogIn failed" + it.exception)
                            progressBar.isVisible = false
                            btLogin.isClickable = true
                            tvSignUp.isClickable = true
                            inputEmail.isFocusable = true
                            inputPass.isFocusable = true
                        }
                    }
                }
            }
        }
    }
}