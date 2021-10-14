package com.example.ximageappx.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentProfileBinding
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import com.example.ximageappx.showToast


class ProfileFragment constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_profile) {

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            context?.showToast("Photo successfully uploaded")
            firebaseService.uploadImageToFirebaseStorage(result.uriContent) {
                firebaseService.setProfilePhoto(it)
            }
        } else {
            context?.showToast(result.error.toString())
        }
    }

    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProfileBinding.bind(view)

        binding.apply {

            firebaseService.updateUser { user ->
                inputProfEmail.setText(user.email)
                inputProfLogin.setText(user.login)
                Glide.with(profileImage).load(user.profilePhotoUrl)
                    .error(R.drawable.default_profile_image).circleCrop().into(profileImage)
            }

            btSave.setOnClickListener {
                if (inputProfLogin.text.toString() != "") {
                    firebaseService.setUserLogin(inputProfLogin.text.toString())
                    context?.showToast("Saved")
                }
            }

            btResetPw.setOnClickListener {
                firebaseService.resetPass()
                context?.showToast("Password reset link was sent to your email")
            }

            btExit.setOnClickListener {
                firebaseService.signOut()
                findNavController().navigateUp()
            }

            profileImage.setOnClickListener {
                startCrop()
            }
        }
    }
}