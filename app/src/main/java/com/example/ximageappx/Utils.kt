package com.example.ximageappx

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun String.isEmailValid(): Boolean {
    return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPassValid(): Boolean =
    length >= 8 /* && this.contains(Regex("""a-zA-Z""")) && this.contains(Regex("""0-9""")) */

fun Context.showToast(text: String) {
    val duration: Int = if (text.length <= 50) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(this, text, duration).show()
}