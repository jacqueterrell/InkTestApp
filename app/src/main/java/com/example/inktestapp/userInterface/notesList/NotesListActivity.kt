package com.example.inktestapp.userInterface.notesList

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.inktestapp.databinding.NoteListLayoutBinding

class NotesListActivity: AppCompatActivity() {

    private lateinit var binding: NoteListLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NoteListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}