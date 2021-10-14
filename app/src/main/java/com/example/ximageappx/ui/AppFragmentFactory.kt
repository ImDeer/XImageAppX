package com.example.ximageappx.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import com.example.ximageappx.ui.details.DetailsFragment
import com.example.ximageappx.ui.gallery.GalleryFragment
import com.example.ximageappx.ui.profile.ProfileFragment
import javax.inject.Inject

class AppFragmentFactory @Inject constructor(
    private val firebaseService: IFirebaseService
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            ProfileFragment::class.java.name -> {
                ProfileFragment(firebaseService)
            }
            GalleryFragment::class.java.name -> {
                GalleryFragment(firebaseService)
            }
            LogInFragment::class.java.name -> {
                LogInFragment(firebaseService)
            }
            RegisterFragment::class.java.name -> {
                RegisterFragment(firebaseService)
            }
            AddPostFragment::class.java.name -> {
                AddPostFragment(firebaseService)
            }
            DetailsFragment::class.java.name -> {
                DetailsFragment(firebaseService)
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}