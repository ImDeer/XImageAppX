package com.example.ximageappx.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ximageappx.R
import com.example.ximageappx.data.User
import com.example.ximageappx.databinding.FragmentProfileBinding
import com.example.ximageappx.services.IFirebaseService
import com.example.ximageappx.showToast


class ProfileFragment constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_profile) {

//    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
//        if (result.isSuccessful) {
//            firebaseService.uploadImageToFirebaseStorage(result.uriContent) {
//                firebaseService.setProfilePhoto(it)
//                context?.showToast("Photo successfully uploaded")
//            }
//        } else {
//            context?.showToast(result.error.toString())
//        }
//    }

//    private fun startCrop() {
//        // start picker to get image for cropping and then use the image in cropping activity
//        cropImage.launch(
//            options {
//                setGuidelines(CropImageView.Guidelines.ON)
//            }
//        )
//    }

    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->//.TakePicture()) { imageUri: Uri? ->
            if (imageUri != null)
                firebaseService.uploadImageToFirebaseStorage(imageUri) {
                    firebaseService.setProfilePhoto(it)
                    context?.showToast("Photo successfully uploaded")
                }
            else
                context?.showToast("Something went wrong, please try again later")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateinit var _user: User

        val binding = FragmentProfileBinding.bind(view)

        binding.apply {

            firebaseService.updateUser { user ->
                inputProfEmail.setText(user.email)
                inputProfLogin.setText(user.login)
//                (activity as MainActivity).supportActionBar?.title = user.login
                Glide.with(profileImage).load(user.profilePhotoUrl)
                    .error(R.drawable.default_profile_image).circleCrop().into(profileImage)
                _user = user
            }

            btSave.setOnClickListener {
                if (inputProfLogin.text.toString() != _user.login) {
                    firebaseService.setUserLogin(inputProfLogin.text.toString())
                    context?.showToast("Saved")
                }
            }

            btResetPw.setOnClickListener {
                firebaseService.resetPass()
                context?.showToast("A password reset link was sent to your email")
            }

            btExit.setOnClickListener {
                firebaseService.signOut()
                findNavController().navigateUp()
            }

            profileImage.setOnClickListener {
//                startCrop()
                getContent.launch("image/*")
            }
        }
    }
}