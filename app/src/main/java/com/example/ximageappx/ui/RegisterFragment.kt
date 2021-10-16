package com.example.ximageappx.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentRegisterBinding
import com.example.ximageappx.isEmailValid
import com.example.ximageappx.isPassValid
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import com.example.ximageappx.showToast
import javax.inject.Inject

class RegisterFragment @Inject constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRegisterBinding.bind(view)

        binding.apply {
            setHasOptionsMenu(false)

            tvSignIn.setOnClickListener {
                val action = RegisterFragmentDirections.actionRegisterFragmentToLogInFragment()
                findNavController().navigate(action)
            }

            btRegister.setOnClickListener {
                progressBar.isVisible = true
                val email = inputRegEmail.text.toString().trim { it <= ' ' }
                val login = inputRegLogin.text.toString().trim { it <= ' ' }
                val pass = inputRegPass.text.toString().trim { it <= ' ' }

                if (!email.isEmailValid())
                    context?.showToast(getString(R.string.enter_valid_email))
                else if (!pass.isPassValid())
                    context?.showToast(getString(R.string.enter_valid_pass))
                else if (login.isEmpty())
                    context?.showToast(getString(R.string.enter_login))
                else
                    firebaseService.register(email, pass, login, {
                        context?.showToast(getString(R.string.signup_sucsess))
                        val action =
                            RegisterFragmentDirections.actionRegisterFragmentToGalleryFragment()
                        progressBar.isVisible = false
                        findNavController().navigate(action)
                    }, { errorMessage ->
                        progressBar.isVisible = false
                        context?.showToast(errorMessage)
                    })
            }
        }
    }
}
