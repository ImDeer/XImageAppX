package com.example.ximageappx.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize
//import kotlinx.android.parcel.Parcelize
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@Parcelize
data class PhotoPost(
    var id: String = "",
    var description: String = "",
    var created: String = "",
    var user: String = "",//: User = User("", "", ""),
    var url: String = ""
) : Parcelable
