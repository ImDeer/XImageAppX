package com.example.ximageappx.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentAddPostBinding
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import com.example.ximageappx.showToast
import javax.inject.Inject

class AddPostFragment @Inject constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_add_post) {

    private val args by navArgs<AddPostFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddPostBinding.bind(view)

        binding.apply {

            val uri = args.uri

            ivNewImage.setImageURI(uri)

            btAddPost.setOnClickListener {
                inputDescription.isFocusable = false
                btAddPost.isClickable = false
                progressBar.isVisible = true
                val description = inputDescription.text.toString()
                firebaseService.uploadImageToFirebaseStorage(uri) {
                    firebaseService.createPost(it, description)
                    context?.showToast("Post added")
                    val action = AddPostFragmentDirections.actionAddPostFragmentToGalleryFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }
}