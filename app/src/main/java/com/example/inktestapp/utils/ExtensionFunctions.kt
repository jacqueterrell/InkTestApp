package com.example.inktestapp.utils

import android.content.Context
import android.net.Uri
import android.view.View
import com.myscript.iink.Editor
import com.myscript.iink.MimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


suspend fun Context.processUriFile(uri: Uri, file: File, logic: (File) -> Unit) {
    withContext(Dispatchers.IO) {
        runCatching {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }
    try {
        logic(file)
    } finally {
        file.deleteOnExit()
    }
}

fun Editor.getWordCount(): Int {
    val text = this.export_(null, MimeType.TEXT) as String
    val wordCount = text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    return wordCount
}

fun Editor.getText(): String {
    val text = this.export_(null, MimeType.TEXT).trim()
    return text
}

fun View.fadeIntoVisibility() {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(500)
        .setListener(null)
}

fun View.fadeOutToInvisible() {
    animate()
        .alpha(0f)
        .setDuration(500)
        .withEndAction {
            visibility = View.INVISIBLE
        }
}