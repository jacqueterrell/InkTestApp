package com.example.inktestapp.userInterface.editNote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.inktestapp.data.NotesEntity
import com.example.inktestapp.userInterface.createNote.AlterNoteBaseActivity

class EditNoteActivity : AlterNoteBaseActivity() {
    override fun isEditingNote(): Boolean = true

    override fun getTag(): String = EditNoteActivity::class.java.simpleName

    override fun getNoteTitle(): String = "Edit Note"

    override fun getEditingNoteEntity(): NotesEntity? = editingNotesEntity

    private lateinit var editingNotesEntity: NotesEntity


    override fun onCreate(savedInstanceState: Bundle?) {
        editingNotesEntity = intent.getParcelableExtra("note")!!
        super.onCreate(savedInstanceState)
    }

    companion object {
        private val TAG = EditNoteActivity::class.java.simpleName
        fun newIntent(context: Context, notesEntity: NotesEntity): Intent {
            return Intent(context, EditNoteActivity::class.java).apply {
                putExtra("note", notesEntity)
            }
        }
    }
}