package com.example.inktestapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.myscript.iink.uireferenceimplementation.EditorView
import androidx.core.graphics.createBitmap
import com.example.inktestapp.R
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.databinding.CreateNoteLayoutBinding
import com.myscript.iink.Editor
import com.myscript.iink.PointerTool
import com.myscript.iink.uireferenceimplementation.EditorData
import java.io.File
import java.io.FileOutputStream
import androidx.core.graphics.toColorInt

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

    fun setExistingCachedNoteUI(editor: Editor?, notesEntity: NotesEntity, binding: CreateNoteLayoutBinding) {
        val inkColor = if (notesEntity.isDarkTheme) {
            binding.editorView.setBackgroundColor(Color.BLACK)
            binding.layoutNotesToText.setBackgroundColor(Color.BLACK)
            binding.noteDivider.setBackgroundColor("#66ffffff".toColorInt())
            binding.tvNotesToText.setTextColor(Color.WHITE)
            binding.tvNotesToText.setHintTextColor("#66ffffff".toColorInt())
            "#ffffff"
        } else {
            binding.editorView.setBackgroundColor(Color.WHITE)
            binding.layoutNotesToText.setBackgroundColor(Color.WHITE)
            binding.noteDivider.setBackgroundColor("#66000000".toColorInt())
            binding.tvNotesToText.setTextColor(Color.BLACK)
            binding.tvNotesToText.setHintTextColor("#66000000".toColorInt())
            "#000000"
        }
        val linesColor = if (notesEntity.isDarkTheme) {
            "#66ffffff"
        } else {
            "#66000000"
        }
        editor?.toolController?.setToolStyle(
            PointerTool.PEN,"color: $inkColor; -myscript-pen-width: 1.0; -myscript-pen-pressure-sensitivity: ${notesEntity.scalingValue}; -myscript-guidelines-color: $linesColor"
        )
    }

    fun setNewlyCreatedNoteUI(editor: Editor?, sharedPrefs: AppSharedPrefs, binding: CreateNoteLayoutBinding) {
        val inkColor = if (sharedPrefs.hasEnabledDarkTheme) {
            binding.editorView.setBackgroundColor(Color.BLACK)
            binding.layoutNotesToText.setBackgroundColor(Color.BLACK)
            binding.noteDivider.setBackgroundColor("#66ffffff".toColorInt())
            binding.tvNotesToText.setTextColor(Color.WHITE)
            binding.tvNotesToText.setHintTextColor("#66ffffff".toColorInt())
            "#ffffff"
        } else {
            binding.editorView.setBackgroundColor(Color.WHITE)
            binding.layoutNotesToText.setBackgroundColor(Color.WHITE)
            binding.noteDivider.setBackgroundColor("#66000000".toColorInt())
            binding.tvNotesToText.setTextColor(Color.BLACK)
            binding.tvNotesToText.setHintTextColor(Color.WHITE)
            binding.tvNotesToText.setHintTextColor("#66000000".toColorInt())
            "#000000"
        }
        val linesColor = if (sharedPrefs.hasEnabledDarkTheme) {
            "#ffffff"
        } else {
            "#66000000"
        }
        editor?.toolController?.setToolStyle(
            PointerTool.PEN,"color: $inkColor; -myscript-pen-width: 1.0; -myscript-pen-pressure-sensitivity: ${sharedPrefs.penSensitivityValue}; -myscript-guidelines-color: $linesColor"
        )
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
    fun extractKeySentences(text: String, topN: Int = 5): List<String> {
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