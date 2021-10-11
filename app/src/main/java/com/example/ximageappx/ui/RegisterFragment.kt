package com.example.ximageappx.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentRegisterBinding
import com.example.ximageappx.isEmailValid
import com.example.ximageappx.isPassValid
import com.example.ximageappx.services.IFirebaseService
import com.example.ximageappx.services.RegisterFailedException
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
                val email = inputRegEmail.text.toString().trim { it <= ' ' }
                val login = inputRegLogin.text.toString().trim { it <= ' ' }
                val pass = inputRegPass.text.toString().trim { it <= ' ' }

                if (!email.isEmailValid())
                    context?.showToast("Please enter valid Email")
                else if (!pass.isPassValid())
                    context?.showToast("Please enter valid password. Password shout be at least 8 characters long and contain letters and numbers")
                else if (login.isEmpty())
                    context?.showToast("Please enter Login")
                else
                    firebaseService.register(email, pass, login) {
                        try {
                            context?.showToast("SignUp successful")
                            val action =
                                RegisterFragmentDirections.actionRegisterFragmentToGalleryFragment()
                            findNavController().navigate(action)
                        } catch (e: RegisterFailedException) {
                            context?.showToast(e.message.toString())
                        }
                    }
            }
        }
    }
}