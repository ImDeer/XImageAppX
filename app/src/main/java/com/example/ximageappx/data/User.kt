package com.example.ximageappx.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var email: String = "",
    var login: String = "",
//    var likedPosts: List<Pair<String, Boolean>> = emptyList(),
    var profilePhotoUrl : String = ""
) : Parcelable