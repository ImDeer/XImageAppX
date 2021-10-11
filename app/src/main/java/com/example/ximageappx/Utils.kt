package com.example.ximageappx

import android.content.Context
import android.text.TextUtils
import android.widget.Toast

fun String.isEmailValid(): Boolean {
    return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPassValid(): Boolean {
    return this.length >= 8 && this.contains("a-z") && this.contains("1-9")
}


fun Context.showToast(text: String) {
    val duration: Int = if (text.length <= 50) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(this, text, duration).show()
}