package com.example.ximageappx.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentAddPostBinding
import com.example.ximageappx.services.IFirebaseService
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
                Toast.makeText(context, "btaddClicked", Toast.LENGTH_SHORT).show()
            }

        }
    }
//    private fun uploadImageToFirebaseStorage(uri: Uri?) {
//        if (uri == null) return
//        val filename = UUID.randomUUID().toString()
//        val mStorage = FirebaseStorage.getInstance().getReference("/images/$filename")
//        mStorage.putFile(uri).addOnSuccessListener {
//            Toast.makeText(context, "Photo successfully uploaded", Toast.LENGTH_SHORT).show()
//            mStorage.downloadUrl.addOnSuccessListener {
////                mUser.child("profilePhotoUrl").setValue(it.toString())
//            }
//        }
//    }

}