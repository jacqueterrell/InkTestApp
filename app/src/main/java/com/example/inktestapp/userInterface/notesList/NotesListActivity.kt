package com.example.inktestapp.userInterface.notesList

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inktestapp.data.AppDataBase
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.databinding.NoteListLayoutBinding
import com.example.inktestapp.userInterface.createNote.CreateNoteActivity
import com.myscript.iink.Editor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesListActivity: AppCompatActivity() {

    private lateinit var binding: NoteListLayoutBinding
    private lateinit var editor: Editor
    private var notesAdapter: NotesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NoteListLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpClickListeners()
    }

    override fun onResume() {
        super.onResume()
        setUpUI()
    }

    private fun setUpUI() {
        val db = AppDataBase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val notesList = db.notesDao().getNotes()

            withContext(Dispatchers.Main) {
                if (notesList.isEmpty()) {
                    binding.fabAddNote.visibility = View.GONE
                    binding.layoutEmptyNotes.visibility = View.VISIBLE
                } else {
                    binding.fabAddNote.visibility = View.VISIBLE
                    binding.layoutEmptyNotes.visibility = View.GONE
                }

                notesAdapter = NotesAdapter(notesList, { notesEntity ->
                    onNoteSelected(notesEntity)
                })
                binding.rvNotes.apply {
                    layoutManager = GridLayoutManager(context, 2)
                    adapter = notesAdapter
                }

            }
        }
    }

    private fun onNoteSelected(notesEntity: NotesEntity) {
        //todo pass the selected note ot the [EditNote]
    }

    private fun setUpClickListeners() {
        binding.btnCreateNote.setOnClickListener {
            val intent = CreateNoteActivity.newIntent(this)
            startActivity(intent)
        }
        binding.fabAddNote.setOnClickListener {

        }
    }
}