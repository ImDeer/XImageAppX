package com.example.ximageappx.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ximageappx.R
import com.example.ximageappx.data.User
import com.example.ximageappx.databinding.FragmentProfileBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mUser =
        FirebaseDatabase.getInstance().getReference("users/" + mAuth.currentUser!!.uid)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lateinit var user: User

        val binding = FragmentProfileBinding.bind(view)

        binding.apply {

            mUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    user = dataSnapshot.getValue(User::class.java)!!
                    inputProfEmail.setText(user.email)
                    inputProfLogin.setText(user.login)
                    Glide.with(profileImage).load(user.profilePhotoUrl)
                        .error(R.drawable.default_profile_image).circleCrop().into(profileImage)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.message)
                }
            })

            btSave.setOnClickListener {
                if (inputProfLogin.text.toString() != user.login)
                    mUser.child("login").setValue(inputProfLogin.text.toString())
            }

            btResetPw.setOnClickListener { mAuth.sendPasswordResetEmail(mAuth.currentUser!!.email!!) }
            btExit.setOnClickListener {
                mAuth.signOut()
                findNavController().navigateUp()
            }

            profileImage.setOnClickListener {

                ImagePicker.with(this@ProfileFragment).cropSquare()
                    .createIntent { intent -> startActivityForResult(intent, 0) }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!
                uploadImageToFirebaseStorage(uri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebaseStorage(uri: Uri?) {
        if (uri == null) return
        val filename = UUID.randomUUID().toString()
        val mStorage = FirebaseStorage.getInstance().getReference("/images/$filename")
        mStorage.putFile(uri).addOnSuccessListener {
            Toast.makeText(context, "Photo successfully uploaded", Toast.LENGTH_SHORT).show()
            mStorage.downloadUrl.addOnSuccessListener {
                mUser.child("profilePhotoUrl").setValue(it.toString())
            }
        }
    }

}