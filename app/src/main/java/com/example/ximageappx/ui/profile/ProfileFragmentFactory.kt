package com.example.ximageappx.ui.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.example.ximageappx.services.auth.IAuthService
import com.example.ximageappx.services.firebase.IDatabaseService
import javax.inject.Inject

class ProfileFragmentFactory @Inject constructor(
    private val databaseService: IDatabaseService,
    private val authService: IAuthService

):FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {
            ProfileFragment::class.java.name->{
                ProfileFragment(databaseService, authService)
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}