package com.example.ximageappx.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentAddPostBinding
import com.example.ximageappx.ui.details.DetailsFragmentArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.FirebaseStorage
//import kotlinx.android.synthetic.main.fragment_add_post.*
import java.util.*

class AddPostFragment : Fragment(R.layout.fragment_add_post) {

    //    var _uri:Uri? = null
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


//        if (_uri == null){
//            getImage()
////            if(_uri!=null)
//        }


    }

//    fun getImage():Boolean {
////        var uri: Uri? = null
////        ImagePicker.with(this@AddPostFragment).crop()
////            .createIntent { intent -> startActivityForResult(intent, 0) }
//        return true
//    }

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