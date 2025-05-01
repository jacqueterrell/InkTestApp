package com.example.inktestapp.userInterface.notesList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.inktestapp.databinding.NoteListLayoutBinding
import com.example.inktestapp.userInterface.AppApplication
import com.myscript.iink.Editor

class NotesListActivity: AppCompatActivity() {

    private lateinit var binding: NoteListLayoutBinding
    private lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NoteListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setUpUI()
    }

    private fun setUpUI() {
        val engine = AppApplication.getEngine()
        editor = engine.createEditor(binding.editorView.renderer!!)
        binding.editorView.editor = editor
        // Optionally set input mode (text, math, diagram, etc.)
        val contentPart = engine.createPackage("memoryPackage").createPart("Text")
        editor.part = contentPart
    }
}