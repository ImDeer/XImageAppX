package com.example.ximageappx.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoPost(
    var id: String = "",
    var description: String = "",
    var user: String = "",//: User = User("", "", ""),
    var url: String = ""
) : Parcelable
