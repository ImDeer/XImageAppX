package com.example.ximageappx.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentLoginBinding
import com.example.ximageappx.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        val binding = FragmentProfileBinding.bind(view)

        binding.apply {
            bt_exit.setOnClickListener {
                mAuth.signOut()
                findNavController().navigateUp()
            }
        }

    }
}