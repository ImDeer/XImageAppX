package com.example.ximageappx.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoPost(
    var id: String = "",
    var description: String = "",
    var created: String = "",
    var user: String = "",
    var url: String = ""
) : Parcelable
