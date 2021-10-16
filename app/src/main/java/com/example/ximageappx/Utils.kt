package com.example.ximageappx

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import java.io.*
import java.util.*

fun String.isEmailValid(): Boolean {
    return this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isPassValid(): Boolean =
    length >= 8

fun Context.showToast(text: String) {
    val duration: Int = if (text.length <= 50) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    Toast.makeText(this, text, duration).show()
}

fun bitmapToUri(bitmap: Bitmap, context: Context): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver,
        bitmap,
        "Title",
        null
    )
    return Uri.parse(path)
}