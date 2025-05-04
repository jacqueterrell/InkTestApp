package com.example.inktestapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import com.myscript.iink.uireferenceimplementation.EditorView
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream

object EditorUtils {

    @ColorInt
    private var backgroundColor = Color.WHITE

    private val stopwords = setOf(
        "the", "is", "at", "which", "on", "a", "an", "and", "in", "of", "to", "it", "that", "we", "for", "as", "with", "was", "were", "this", "be", "by", "are", "from"
    )

    fun getBitmapFromEditorView(editorView: EditorView): Bitmap {
        val bitmap = createBitmap(editorView.width, editorView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawARGB(
            Color.alpha(backgroundColor),
            Color.red(backgroundColor),
            Color.green(backgroundColor),
            Color.blue(backgroundColor)
        )
        editorView.draw(canvas)
        return bitmap
    }

    fun saveBitMap(context: Context, bitmap: Bitmap, filename: String): File {
        val file = File(context.getExternalFilesDir(null), filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun retrieveSavedBitMap(context: Context, filename: String): Bitmap? {
        val imageFile = File(context.getExternalFilesDir(null), filename)
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            return bitmap
        } else {
            Log.e("Editor", "Image file not found: ${imageFile.absolutePath}")
            return null
        }
    }




    // Splits text into sentences
    private fun splitIntoSentences(text: String): List<String> {
        return text.trim().split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
    }

    // Tokenizes and filters a sentence into meaningful words
    private fun tokenize(sentence: String): List<String> {
        return sentence.lowercase()
            .split(Regex("\\W+"))
            .filter { it.isNotBlank() && it !in stopwords }
    }

    // Main method to extract key sentences
    fun extractKeySentences(text: String, topN: Int = 3): List<String> {
        val sentences = splitIntoSentences(text)
        val sentenceScores = sentences.map { sentence ->
            val words = tokenize(sentence)
            val score = words.groupingBy { it }.eachCount()
                .filter { it.value > 1 }.values.sum() // You can tweak this scoring
            sentence to score
        }

        return sentenceScores
            .sortedByDescending { it.second }
            .take(topN)
            .map { it.first }
    }

}