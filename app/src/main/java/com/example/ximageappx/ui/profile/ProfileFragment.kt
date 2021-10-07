package com.example.ximageappx.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ximageappx.R
import com.example.ximageappx.data.User
import com.example.ximageappx.databinding.FragmentProfileBinding
import com.example.ximageappx.services.auth.IAuthService
import com.example.ximageappx.services.firebase.IDatabaseService
import javax.inject.Inject


class ProfileFragment constructor(
    private val databaseService: IDatabaseService,
    private val authService: IAuthService
) : Fragment(R.layout.fragment_profile) {
    // = FirebaseAuthService()
     //= FirebaseDatabaseService()

//    @Inject
//    lateinit var databaseService: IDatabaseService
//    @Inject
//    lateinit var authService: IAuthService


    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->//.TakePicture()) { imageUri: Uri? ->
            if (imageUri != null)
                databaseService.uploadImageToFirebaseStorage(imageUri) {
                    Toast.makeText(context, "Photo successfully uploaded", Toast.LENGTH_SHORT)
                        .show()
                }
            else
                Toast.makeText(
                    context,
                    "Something went wrong, please try again later",
                    Toast.LENGTH_SHORT
                ).show()
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lateinit var _user: User

        val binding = FragmentProfileBinding.bind(view)

        binding.apply {

            databaseService.updateUser { user ->
                inputProfEmail.setText(user.email)
                inputProfLogin.setText(user.login)
                Glide.with(profileImage).load(user.profilePhotoUrl)
                    .error(R.drawable.default_profile_image).circleCrop().into(profileImage)
                _user = user
            }

            btSave.setOnClickListener {
                if (inputProfLogin.text.toString() != _user.login) {
                    databaseService.setUserLogin(inputProfLogin.text.toString())
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                }
            }

            btResetPw.setOnClickListener {
                authService.resetPass()
                //TODO make toast message
                Toast.makeText(context, "Password reset email is sent to your ", Toast.LENGTH_SHORT)
                    .show()
            }

            btExit.setOnClickListener {
                authService.signOut()
                findNavController().navigateUp()
            }

            profileImage.setOnClickListener {
                getContent.launch("image/*")
            }
        }
    }
}