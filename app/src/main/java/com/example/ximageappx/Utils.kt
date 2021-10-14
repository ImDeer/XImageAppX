package com.example.ximageappx

import android.content.Context
import android.widget.Toast

fun String.isEmailValid(): Boolean {
    return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPassValid(): Boolean =
    length >= 8

fun Context.showToast(text: String) {
    val duration: Int = if (text.length <= 50) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(this, text, duration).show()
}
