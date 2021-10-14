package com.example.ximageappx.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var email: String = "",
    var login: String = "",
    var profilePhotoUrl: String = ""
) : Parcelable