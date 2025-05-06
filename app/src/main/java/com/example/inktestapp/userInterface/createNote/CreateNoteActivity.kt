package com.example.inktestapp.userInterface.createNote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.inktestapp.data.NotesEntity


class CreateNoteActivity : AlterNoteBaseActivity() {
    override fun isEditingNote(): Boolean = false

    override fun getTag(): String = CreateNoteActivity::class.java.simpleName

    override fun getNoteTitle(): String = "Create Note"

    override fun getEditingNoteEntity(): NotesEntity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        private val TAG = CreateNoteActivity::class.java.simpleName
        fun newIntent(context: Context): Intent {
            return Intent(context, CreateNoteActivity::class.java)
        }
    }
}